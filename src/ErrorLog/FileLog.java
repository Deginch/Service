package ErrorLog;

import Tool.JarTool;

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
 * Created by root on 16-10-26.
 */
public class FileLog implements Log {
    private static String dir;
    private static File logFile;
    private static PrintWriter writer;
    private static Calendar calendar = new GregorianCalendar();
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyMMdd");
    private static SimpleDateFormat logFormat = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
    private static Date date = new Date();

    public FileLog(String serviceName) {
        FileLog.dir = JarTool.getJarDir() + "/log/" + serviceName + '/';
        initFile();
        addOneDay();
    }

    @Override
    public void writeLog(String message, int level) {
        Date now = new Date();
        if (new Date().after(date)) {
            initFile();
            addOneDay();
        }
        writer.println("level:"+level+" ---------------------------------------------------------" + logFormat.format(now));
        writer.println(message);
        writer.flush();
    }

    @Override
    public void writeLog(String message, Throwable e, int level) {
        Date now = new Date();
        if (now.after(date)) {
            initFile();
            addOneDay();
        }
        writer.println("level:"+level+" ---------------------------------------------------------" + logFormat.format(now));
        writer.println(message);
        e.printStackTrace(writer);
        writer.flush();
    }

    @Override
    public void writeLog(Throwable e, int level) {
        Date now = new Date();
        if (new Date().after(date)) {
            initFile();
            addOneDay();
        }
        writer.println("level:"+level+" ---------------------------------------------------------" + logFormat.format(now));
        e.printStackTrace(writer);
        writer.flush();
    }

    @Override
    public void close() {
        logFile = null;
        if (writer != null) {
            writer.close();
            writer = null;
        }
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
}
