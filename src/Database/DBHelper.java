package Database;

import Service.*;
import Tool.ErrorLog;
import com.mysql.jdbc.exceptions.MySQLIntegrityConstraintViolationException;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.*;

/**
 * Created by degin on 2016/6/30.
 * 数据库通用操作类
 */
public class DBHelper {

    Connection conn;
    private Object object = new Object();

    public DBHelper() throws SQLException {
        openConn();
    }

    public DBHelper(Connection conn) {
        this.conn = conn;
    }

    public void openConn() throws SQLException {
        JdbcPool jdbcPool = new JdbcPool();
        if (conn != null) {
            conn.close();
        }
        conn = jdbcPool.getConnection();
    }

    public void reOpen() {
        try {
            Thread.sleep(3000);
            openConn();
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
    }

    public Object queryMap(String sql, Object[] params, ResultMapper mapper) throws SQLException {
        synchronized (object) {
            PreparedStatement statement = null;
            ResultSet rs = null;
            try {
                statement = conn.prepareStatement(sql);
                for (int i = 0; i < params.length; i++) {
                    statement.setObject(i + 1, params[i]);
                }
                rs = statement.executeQuery();
                while (rs.next() && mapper.map(rs)) ;
            } finally {
                release(statement, rs);
            }
            return mapper.result();
        }
    }

    public int execute(String sql, Object[] params) throws SQLException {
        synchronized (object) {
            PreparedStatement statement = null;
            int changedCount = 0;
            try {
                statement = conn.prepareStatement(sql);
                for (int i = 0; i < params.length; i++) {
                    statement.setObject(i + 1, params[i]);
                }
                changedCount = statement.executeUpdate();
            } finally {
                release(statement);
            }
            return changedCount;
        }
    }

    /**
     * 关闭链接
     */
    public void close() {
        try {
            if (conn != null)
                conn.close();
        } catch (SQLException e) {
            ErrorLog.writeLog(e);
        }
    }

    /**
     * @Date 2016/7/1
     * 释放资源
     */
    public static void release(Statement statement, ResultSet rs) {
        release(rs);
        release(statement);
    }

    /**
     * @Date 2016/7/1
     * 释放资源
     */
    public static void release(Statement statement) {
        if (statement != null) {
            try {
                statement.close();
                statement = null;
            } catch (SQLException e) {
                ErrorLog.writeLog(e);
            }
        }
    }

    /**
     * @Date 2016/7/1
     * 释放资源
     */
    public static void release(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
                rs = null;
            } catch (SQLException e) {
                ErrorLog.writeLog(e);
            }
        }
    }
}
