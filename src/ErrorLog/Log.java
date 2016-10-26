package ErrorLog;

/**
 * Created by root on 16-10-26.
 */
public interface Log {
    void writeLog(String message, int level);

    void writeLog(String message, Throwable e, int level);

    void writeLog(Throwable e, int level);

    void close();
}
