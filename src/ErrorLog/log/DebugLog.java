package ErrorLog.log;

import ErrorLog.*;

/**
 * Created by root on 16-10-26.
 */
public class DebugLog implements Log {
    @Override
    public void writeLog(String message, int level) {
        System.out.println(message);
    }

    @Override
    public void writeLog(String message, Throwable e, int level) {
        System.out.println(message);
        e.printStackTrace();
    }

    @Override
    public void writeLog(Throwable e, int level) {
        e.printStackTrace();
    }

    @Override
    public void close() {

    }
}
