package Tool;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by sheldon on 16-9-30.
 */
public class ThreadPool {
    private static ExecutorService cachedThreadPool;

    public static synchronized void init() {
        if (cachedThreadPool == null) {
            cachedThreadPool = Executors.newCachedThreadPool();
        }
    }

    public static synchronized void exec(Runnable thread) {
        cachedThreadPool.execute(thread);
    }

    /**
     * 停止所有正在运行的进程
     *
     * @return
     */
    public static synchronized List<Runnable> shutdownNow() {
        return cachedThreadPool.shutdownNow();
    }

    /**
     * 等待所有进程停止
     * @param second
     */
    public static void waitTerminate(int second){
        try {
            cachedThreadPool.awaitTermination(second, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            ErrorLog.writeLog(e);
        }
    }
}
