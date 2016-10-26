import AutoSocket.*;
import ErrorLog.ErrorLog;
import ErrorLog.log.DatabaseLog;
import Tool.FileUtil;

import java.io.*;
import java.util.Date;

/**
 * Created by degin on 2016/7/3.
 */
public class MyMain {
    static AutoTcp src, des;

    public static void main(String[] agrs) {
        ErrorLog.setDebug();
        File test=new File("/home/tes11223t");
        try {
            BufferedReader reader=new BufferedReader(new FileReader(test));
        } catch (FileNotFoundException e) {
            System.out.println(DatabaseLog.getMessage(e));
        }
    }

    public static void ServerChannelTest() {
        ErrorLog.setDebug();
        AutoTcp server = AutoTcp.GetSocketByProtocolType(AutoTcp.TcpClientProtocol, "192.168.0.145", 31001, new TcpReceiver() {
            @Override
            public void GetData(byte[] data) {
                System.out.println(new String(data));
            }

        }).setRestartTime(1);
        server.start();

        while (true) {
//            new AutoClientChannel("127.0.0.1", 31001, new desReceiver()).start();
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
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

class srcReceiver implements TcpReceiver {

    @Override
    public void GetData(byte[] data) {
        System.out.println(new String(data));
    }

}

class desReceiver implements TcpReceiver {

    @Override
    public void GetData(byte[] data) {
        System.out.println(new String(data));
    }

}