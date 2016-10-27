
import ErrorLog.ErrorLog;
import ErrorLog.log.DatabaseLog;
import Tool.FileUtil;

import java.io.*;
import java.util.Date;

/**
 * Created by degin on 2016/7/3.
 */
public class MyMain {

    public static void main(String[] agrs) {
        ErrorLog.setDebug();
        File test=new File("/home/tes11223t");
        try {
            BufferedReader reader=new BufferedReader(new FileReader(test));
        } catch (FileNotFoundException e) {
            System.out.println(DatabaseLog.getMessage(e));
        }
    }

    public static void ErrorLogTest() {
        String test = FileUtil.getFileData(1, new File("/home/sheldon/mycode/jar/rtklib/conf/test_opt.conf")).toString();
        StringBuffer buffer = new StringBuffer(test);
        for (int i = 1; i < 100; i++) {
            buffer.append(test);
        }
        test = buffer.toString();
        Date date = new Date();
        while (true) {
            ErrorLog.writeLog(test);
            Date now = new Date();
            System.out.println(now.getTime() - date.getTime());
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    public static void tcpTest() {
    }


}