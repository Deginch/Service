
import AutoStream.*;
import ErrorLog.ErrorLog;
import Tool.FileUtil;
import Tool.ThreadPool;

import java.io.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by degin on 2016/7/3.
 */
public class MyMain {

    public static void main(String[] agrs) throws IOException, InterruptedException {
        ErrorLog.setDebug();
        ThreadPool.init();
        tcpTest();
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


    public static void tcpTest() throws IOException, InterruptedException {

        NioStream nioStream = NioTcp.getNioTcp(AutoStream.TcpClient, "127.0.0.1", 11111, new StreamReceiver() {
            @Override
            public Object read(byte[] data) {
                ErrorLog.writeLog(new String(data)+'\n');
                ErrorLog.writeLog(data[0]+"\n");
                return (int)data[0];
            }
        }).setRestartTime(1).getNioStream();
        nioStream.start();
        int n = 0;
        while (true) {
//            nioStream.send(("test:" + n++ + "\n").getBytes(),49);
            Thread.sleep(1000);
        }
    }


}