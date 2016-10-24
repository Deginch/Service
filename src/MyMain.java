import AutoSocket.*;
import Database.*;
import Service.Service;
import Service.ServiceFactory;
import Service.ServiceManager;
import Tool.ErrorLog;
import Tool.FileUtil;
import Tool.JarTool;
import com.mysql.jdbc.exceptions.MySQLIntegrityConstraintViolationException;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.Date;
import java.util.logging.FileHandler;


/**
 * Created by degin on 2016/7/3.
 */
public class MyMain {
    static AutoTcp src, des;

    public static void main(String[] agrs) {
        ErrorLog.setDebug();
        JdbcPool pool = new JdbcPool();
        PreparedStatement statement = null;
        int changedCount = 0;
        try {
            Connection conn = pool.getConnection();
            String sql = "insert into table1 (object_id)VALUES (?);";
            try {
                statement = conn.prepareStatement(sql);
                    for (int i = 10000; i < 110000; i++) {
                        statement.clearParameters();
                        statement.setObject(1, i/10000);
                        changedCount = statement.executeUpdate();
                    }

            } catch (MySQLIntegrityConstraintViolationException e) {
                ErrorLog.writeLog(e);
            } catch (SQLException e) {
                ErrorLog.writeLog(e);
            } finally {
            }
        } catch (SQLException e) {
            ErrorLog.writeLog(e);
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
        ErrorLog.setDir("/home/sheldon/mycode/jar/");
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