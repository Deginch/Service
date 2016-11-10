package Service;

import Database.*;
import Database.ReflectStuff.Column;
import Database.ReflectStuff.Table;
import ServiceHandler.*;
import ErrorLog.ErrorLog;
import Tool.ThreadPool;

import java.sql.SQLException;
import java.util.Hashtable;

/**
 * Created by degin on 2016/6/30.
 * 服务管理类，管理所有服务的重启开关等工作。
 * 也可以作为单个服务类来作为服务管理类的子类
 */
@Table(value = "tb_service_state")
public class ServiceManager implements Commander {

    @Column(isIndex = true)
    public String service_name;
    @Column
    public int service_state = ServiceState.Running.getState();
    @Column
    public int command;

    private Class clazz;
    private Hashtable<Integer, Service> serviceHashtable;
    private volatile boolean isRun = false;
    private String serviceTableName;
    private ServiceFactory serviceFactory;

    public ServiceManager(ServiceFactory serviceFactory) throws InterruptedException {
        while (!testNetwork()) {//假如无法连接数据库，每隔1分钟重启一次
            Thread.sleep(60 * 1000);
        }
        this.clazz = serviceFactory.getServiceClass();
        this.serviceFactory = serviceFactory;
        service_name = serviceFactory.getServiceName();
        serviceTableName = ((Table) clazz.getAnnotation(Table.class)).value();
        ErrorLog.init(service_name, serviceFactory.getLogTypes());
        ThreadPool.init();
        killCallBack();
    }

    /**
     * 连接数据库查看是否可用
     *
     * @return
     */
    private boolean testNetwork() {
        DatabaseHandler handler = null;
        try {
            handler = new DatabaseHandler();
        } catch (SQLException e) {
            System.err.println("网络无法连接\n");
            return false;
        } finally {
            if (handler != null) {
                handler.close();
            }
        }
        return true;
    }


    @Override
    public void start() {

        ErrorLog.writeLog("service serviceStart!");
        isRun = true;
        DatabaseHandler handler = null;
        try {
            handler = new DatabaseHandler();
            serviceHashtable = handler.queryHashTable(serviceFactory, Where.notEqual("changed", -1));
            for (Service service : serviceHashtable.values()) {
                service.serviceStart();
                handler.update(service);
            }
            ThreadPool.exec(new ServiceStateMonitor());
            ThreadPool.exec(new ServiceDynamicLoading());
            this.service_state = ServiceState.Running.getState();
            handler.insertDuplicateUpdate(this);//更新服务检测表
        } catch (SQLException e) {
            ErrorLog.writeLog(e);
        } catch (Exception e) {
            ErrorLog.writeLog(e);
            e.printStackTrace();
        } finally {
            if (handler != null) {
                handler.close();
            }
        }
        Thread.currentThread().setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                ErrorLog.writeLog("get e uncaught exception:", e);
            }
        });
    }

    @Override
    public void stop() {
        ErrorLog.writeLog("service serviceStop!");
        isRun = false;
        DatabaseHandler handler = null;
        try {
            handler = new DatabaseHandler();
            for (Service service : serviceHashtable.values()) {
                service.serviceStop();
                handler.update(service);
            }
            serviceHashtable.clear();
            this.service_state = ServiceState.Sleep.getState();
            handler.insertDuplicateUpdate(this);
        } catch (SQLException e) {
            ErrorLog.writeLog(e);
        } finally {
            if (handler != null) {
                handler.close();
            }
        }

    }

    @Override
    public void reboot() {
        ErrorLog.writeLog("service reboot!");
        DatabaseHandler handler = null;
        try {
            handler = new DatabaseHandler();
            for (Service service : serviceHashtable.values()) {
                service.serviceStop();
                service.setChanged(ServiceChanged.NoChange.getChanged());
                service.setState(ServiceState.Rebooting.getState());
                handler.update(service);
            }
            serviceHashtable.clear();
            this.service_state = ServiceState.Rebooting.getState();
            this.command = ServiceCommand.NoCommand.getCommand();
            handler.update(this);
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                serviceHashtable = handler.queryHashTable(serviceFactory, Where.notEqual("changed", -1));
                for (Service service : serviceHashtable.values()) {
                    service.serviceStart();
                    handler.update(service);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            this.service_state = ServiceState.Running.getState();
            handler.update(this);
        } catch (SQLException e) {
            ErrorLog.writeLog(e);
        } finally {
            if (handler != null) {
                handler.close();
            }
        }
    }

    /**
     * 当接受到linux系统的kill信号时退出服务，释放数据库和日志资源
     */
    private void killCallBack() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                if (isRun) {
                    ErrorLog.writeLog("start exit");
                    ServiceManager.this.stop();
                    ThreadPool.shutdownNow();
                    ThreadPool.waitTerminate(10);
                    ErrorLog.writeLog("exit successfully,start clear");
                    JdbcPool.close();
                    ErrorLog.close();
                }
            }
        });
    }

    /**
     * 服务管理器状态监视器
     */
    class ServiceStateMonitor implements Runnable {

        public void run() {
            DatabaseHandler handler = null;
            try {
                handler = new DatabaseHandler();
                while (isRun) {
                    try {
                        handler.refresh(ServiceManager.this);
                        ServiceCommand.handleCommand(ServiceManager.this, ServiceCommand.parseFromInt(ServiceManager.this.command));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                    }
                }
            } catch (SQLException e) {
                ErrorLog.writeLog(e);
            } finally {
                if (handler != null) {
                    handler.close();
                }
            }
        }

    }

    /**
     * 动态加载线程，循环查询数据库，查看数据是否改动，是则改动相应服务。
     */
    class ServiceDynamicLoading implements Runnable {

        public void run() {
            DBHelper db = null;
            try {
                db = new DBHelper();
                DatabaseHandler handler = new DatabaseHandler(db);
                while (isRun) {
                    try {
                        Hashtable<Integer, Service> changedServiceHashtable = handler.queryHashTable(serviceFactory, Where.bigger("changed", 0).or(Where.isEqual("changed", -1).and(Where.isEqual("state", 1))));

                        for (Service service : changedServiceHashtable.values()) {
                            ServiceChanged.handlerChanged(handler, ServiceManager.this.serviceHashtable, service);
                        }
                    } catch (Exception e) {
                        ErrorLog.writeLog(e);
                    }
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                db.close();
            }
        }

    }

}
