package Database;

import ErrorLog.ErrorLog;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.LinkedList;
import java.util.Properties;
import java.util.logging.Logger;
import javax.sql.DataSource;

/**
 * @ClassName: JdbcPool
 * @Description:编写数据库连接池
 * @author: 孤傲苍狼
 * @date: 2014-9-30 下午11:07:23
 */
public class JdbcPool implements DataSource {

    /**
     * @Field: listConnections
     * 使用LinkedList集合来存放数据库链接，
     * 由于要频繁读写List集合，所以这里使用LinkedList存储数据库连接比较合适
     */
    private static LinkedList<Connection> listConnections = new LinkedList();
    private static volatile int initSize = 0;
    private static String driver;
    private static String url;
    private static String username;
    private static String password;
    private static LinkedList<Connection> usedList=new LinkedList<>();
    static {
        //在静态代码块中加载db.properties数据库配置文件
        InputStream in = null;
        try {
            in = JdbcPool.class.getClassLoader().getResourceAsStream("db.properties");
            Properties prop = new Properties();
            prop.load(in);
            driver = prop.getProperty("driver");
            url = prop.getProperty("url");
            username = prop.getProperty("username");
            password = prop.getProperty("password");
            //数据库连接池的初始化连接数大小
            initSize = Integer.parseInt(prop.getProperty("jdbcPoolInitSize"));
            //加载数据库驱动
            Class.forName(driver);
            for (int i = 0; i < initSize; i++) {
                Connection conn = DriverManager.getConnection(url, username, password);
                //将获取到的数据库连接加入到listConnections集合中，listConnections集合此时就是一个存放了数据库连接的连接池
                listConnections.add(conn);
            }

        } catch (Exception e) {
            throw new ExceptionInInitializerError(e);
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public int getLoginTimeout() throws SQLException {
        // TODO Auto-generated method stub
        return 0;
    }

    /**
     * Return the parent Logger of all the Loggers used by this data source. This
     * should be the Logger farthest from the root Logger that is
     * still an ancestor of all of the Loggers used by this data source. Configuring
     * this Logger will affect all of the log messages generated by the data source.
     * In the worst case, this may be the root Logger.
     *
     * @return the parent Logger for this data source
     * @throws SQLFeatureNotSupportedException if the data source does not use
     *                                         {@code java.util.logging}
     * @since 1.7
     */
    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return null;
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }

    public static synchronized void close(){
        ErrorLog.writeLog("closing all connections");
        while (usedList.size()>0){
            try {
                usedList.remove().close();
            } catch (SQLException e) {
                ErrorLog.writeLog(e);
            }
        }
        for (Connection conn:listConnections) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        System.out.println("all connections closed");
    }

    /* 获取数据库连接
     * @see javax.sql.DataSource#getConnection()
     */
    @Override
    public synchronized Connection getConnection() throws SQLException {
        final Connection conn;
        //从listConnections集合中获取一个数据库连接
        if (listConnections.size() > 0) {
            conn = listConnections.removeFirst();
        } else {
            conn = DriverManager.getConnection(url, username, password);
//            ErrorLog.writeLog("conn is out");
        }
        usedList.add(conn);
//        ErrorLog.writeLog("get new conn, listConnections's count is " + listConnections.size()+",used conn is "+usedList.size());
        //返回Connection对象的代理对象
        return (Connection) Proxy.newProxyInstance(JdbcPool.class.getClassLoader(), conn.getClass().getInterfaces(), new InvocationHandler() {
            @Override
            public synchronized Object invoke(Object proxy, Method method, Object[] args)
                    throws Throwable {
                if (!method.getName().equals("close")) {
                    return method.invoke(conn, args);
                } else {
                    usedList.remove(conn);
//                    ErrorLog.writeLog("stop old conn, listConnections's count is " + listConnections.size()+",used conn is "+usedList.size());
                    if (listConnections.size() >= initSize) {
                        return method.invoke(conn, args);
                    } else {
                        //如果调用的是Connection对象的close方法，就把conn还给数据库连接池
                        listConnections.add(conn);
//                        ErrorLog.writeLog(conn + " returned to listConnections pool！！");
                        return null;
                    }
                }
            }
        });

    }

    @Override
    public Connection getConnection(String username, String password)
            throws SQLException {
        return null;
    }
}