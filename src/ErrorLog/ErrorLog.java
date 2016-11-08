package ErrorLog;

import ErrorLog.log.DatabaseLog;
import ErrorLog.log.DebugLog;
import ErrorLog.log.FileLog;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by degin on 2016/7/25.
 */
public class ErrorLog {
    private static List<Log> logs = new ArrayList<>();
    /**
     * 调试输出
     */
    public static final int DEBUG_LOG = 1 << 0;
    /**
     * 文件输出
     */
    public static final int FILE_LOG = 1 << 1;
    /**
     * 数据库输出
     */
    public static final int DATABASE_LOG = 1 << 2;

    public static boolean init(String serviceName, int logTypes) {
        if ((DEBUG_LOG & logTypes) != 0) {
            logs.add(new DebugLog());
        }
        if ((FILE_LOG & logTypes) != 0) {
            logs.add(new FileLog(serviceName));
        }
        if ((DATABASE_LOG & logTypes) != 0) {
            try {
                logs.add(new DatabaseLog(serviceName));
            } catch (SQLException e) {
                ErrorLog.writeLog(e);
                return false;
            }
        }
        return true;
    }

    public static void setDebug() {
        init("", DEBUG_LOG);
    }

    public static void writeLog(String massage, Throwable e) {
        writeLog(massage, e, 0);
    }

    public static void writeLog(Throwable e) {
        writeLog(e, 0);
    }

    public static void writeLog(String message) {
        writeLog(message, 0);
    }

    public static void writeLog(String message, int level) {
        for (Log log : logs) {
            log.writeLog(message, level);
        }
    }

    public static void writeLog(String massage, Throwable e, int level) {
        for (Log log : logs) {
            log.writeLog(massage, e, level);
        }
    }

    public static void writeLog(Throwable e, int level) {
        for (Log log : logs) {
            log.writeLog(e, level);
        }
    }

    public static void close() {
        for (Log log : logs) {
            log.close();
        }
    }
}
