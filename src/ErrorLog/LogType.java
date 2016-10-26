package ErrorLog;

import ErrorLog.log.*;

/**
 * Created by root on 16-10-26.
 */
public enum  LogType {
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
