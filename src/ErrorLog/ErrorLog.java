package ErrorLog;

/**
 * Created by degin on 2016/7/25.
 */
public class ErrorLog {
    private static Log[] logs;

    public enum LogType {
        DEBUG_LOG, FILE_LOG, DATABASE_LOG;

        public Log getLog(String serviceName) {
            switch (this) {
                case DEBUG_LOG:
                    return new DebugLog();
                case FILE_LOG:
                    return new FileLog(serviceName);
                case DATABASE_LOG:
                    return new DatabaseLog(serviceName);
                default:
                    return new FileLog(serviceName);
            }
        }
    }

    public static void init(String serviceName, LogType ... logTypes) {
        if (logs != null) {
            return;
        }
        logs = new Log[logTypes.length];
        for (int i = 0; i > logTypes.length; i++) {
            logs[i] = logTypes[i].getLog(serviceName);
        }
    }

    public static void setDebug() {
        init("",LogType.DEBUG_LOG);
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
