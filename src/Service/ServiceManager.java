package Service;

import Database.*;
import ServiceHandler.*;
import ErrorLog.ErrorLog;
import Tool.ThreadPool;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.Hashtable;

/**
 * Created by degin on 2016/6/30.
 * 服务管理类，管理所有服务的重启开关等工作。
 * 也可以作为单个服务类来作为服务管理类的子类
 */
public class ServiceManager implements Commander, DatabaseObject {

    public String service_name;
    public int service_state = ServiceState.Running.getState();
    public int command;

    private Class<DatabaseObject> clazz;
    private Hashtable<Integer, Service> serviceHashtable;
    private volatile boolean isRun = false;
    private String serviceTableName;
    private ServiceFactory serviceFactory;

    public ServiceManager(ServiceFactory serviceFactory) {
        this.clazz = serviceFactory.getServiceClass();
        this.serviceFactory = serviceFactory;
        service_name = serviceFactory.getServiceName();
        serviceTableName = serviceFactory.getDefaultService().getTableName();
        ErrorLog.init(service_name,serviceFactory.getLogTypes());
        ThreadPool.init();
        killCallBack();
    }

    @Override
    public void start() {

        //调用服务初始化函数init
        ErrorLog.writeLog("service serviceStart!");
        Method method = null;
        try {
            method = clazz.getMethod("beforeStart");
            method.invoke(null);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        String selectSql = "select * from " + serviceTableName + " where changed != ?;";
        isRun = true;
        DatabaseHandler handler = null;
        try {
            handler = new DatabaseHandler();
            serviceHashtable = handler.queryHashTable(selectSql, new Object[]{-1}, serviceFactory);
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
        Thread.currentThread().setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler(){
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                ErrorLog.writeLog("get e uncaught exception:",e);
            }
        });
    }

    @Override
    public void stop() {
        ErrorLog.writeLog("service serviceStop!");
        //调用服务初始化函数init
        Method method = null;
        try {
            method = clazz.getMethod("afterStop");
            method.invoke(null);
        } catch (NoSuchMethodException e) {
            ErrorLog.writeLog(e);
        } catch (InvocationTargetException e) {
            ErrorLog.writeLog(e);
        } catch (IllegalAccessException e) {
            ErrorLog.writeLog(e);
        }
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
                String selectSql = "select * from " + serviceTableName + " where changed != ?;";
                serviceHashtable =  handler.queryHashTable(selectSql, new Object[]{-1}, serviceFactory);
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
                        handler.select(ServiceManager.this);
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
            String sql = "select * from " + serviceTableName + " where changed > ? or (changed = ? and state = ?);";
            DBHelper db = null;
            try {
                db = new DBHelper();
                DatabaseHandler handler = new DatabaseHandler(db);
                while (isRun) {
                    try {
                        Hashtable<Integer, Service> changedServiceHashtable =  handler.queryHashTable(sql, new
                                Object[]{ServiceChanged
                                .NoChange.getChanged(), ServiceChanged.Deleted.getChanged(), ServiceState.Running.getState()},serviceFactory);

                        for (Service service : changedServiceHashtable.values()) {
                            ThreadPool.exec(new Runnable() {
                                @Override
                                public void run() {
                                    ServiceChanged.handlerChanged(handler, ServiceManager.this.serviceHashtable, service);
                                }
                            });
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

    @Override
    public Field getIndexField() throws NoSuchFieldException {
        return this.getClass().getField("service_name");
    }

    @Override
    public String getTableName() {
        return "tb_service_state";
    }
}
