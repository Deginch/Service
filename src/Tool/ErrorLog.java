package Tool;

import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by degin on 2016/7/25.
 */
public class ErrorLog {
    private static String dir;
    private static File logFile;
    private static PrintWriter writer;
    private static Calendar calendar = new GregorianCalendar();
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyMMdd");
    private static SimpleDateFormat logFormat = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
    private static Date date = new Date();
    private static volatile boolean isDebug = false;

    /**
     * 写入日志
     *
     * @param error
     */
    public static synchronized void writeLog(String error) {
        System.out.println(error);
        if (isDebug) {
            return;
        }
        Date now = new Date();
        if (now.after(date)) {
            initFile();
            addOneDay();
        }
        writer.println("---------------------------------------------------------" + logFormat.format(now));
        writer.println(error);
        writer.flush();
    }

    /**
     * 写入日志
     *
     * @param error
     */
    public static synchronized void writeLog(double error) {
        writeLog(String.valueOf(error));
    }

    public static synchronized void writeLog(Throwable e) {
        e.printStackTrace();
        if (isDebug) {
            return;
        }
        Date now = new Date();
        if (now.after(date)) {
            initFile();
            addOneDay();
        }
        writer.println("---------------------------------------------------------" + logFormat.format(now));
        e.printStackTrace(writer);
        writer.flush();
    }


    public static synchronized void writeLog(String error, Throwable e) {
        System.out.println(error);
        e.printStackTrace();
        if (isDebug) {
            return;
        }
        Date now = new Date();
        if (now.after(date)) {
            initFile();
            addOneDay();
        }
        writer.println("---------------------------------------------------------" + logFormat.format(now));
        writer.println(error);
        e.printStackTrace(writer);
        writer.flush();
    }

    /**
     * 设置是否为调试模式,当为true时，不会输出日志文件
     *
     */
    public static synchronized void setDebug() {
        ErrorLog.isDebug = true;
    }

    /**
     * 给时间戳+1
     */
    private static void addOneDay() {
        calendar.setTime(date);
        calendar.add(Calendar.DATE, 1);
        date = calendar.getTime();
        try {
            date = dateFormat.parse(dateFormat.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化日志文件
     */
    private static void initFile() {
        logFile = null;
        if (writer != null) {
            writer.close();
            writer = null;
        }
        String path = dir + dateFormat.format(date) + ".log";
        System.out.println("ErrorLog path=" + path);
        logFile = new File(path);
        if (!logFile.getParentFile().exists()) {
            logFile.getParentFile().mkdir();
        }
        try {
            if (!logFile.exists()) {
                logFile.createNewFile();
            }
            writer = new PrintWriter(new FileWriter(logFile, true));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static synchronized void close() {
        logFile = null;
        if (writer != null) {
            writer.close();
            writer = null;
        }
    }

    public static void setDir(String strDir) {
        ErrorLog.dir = strDir;
        initFile();
        addOneDay();
    }
}
