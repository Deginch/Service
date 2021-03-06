package ErrorLog.log;

import Database.DatabaseHandler;
import ErrorLog.*;

import java.sql.SQLException;

/**
 * Created by root on 16-10-26.
 */
public class DatabaseLog implements Log {
    private DatabaseHandler handler;

    public DatabaseLog(String serviceName) throws SQLException {
        String tableName = "tb_" + serviceName + "_log";
        LogMessage.initDatabase(tableName);
        String createTable = "CREATE TABLE IF NOT EXISTS `" + tableName + "` (\n" +
                "  `id` int(11) NOT NULL AUTO_INCREMENT,\n" +
                "  `level` int(10) unsigned DEFAULT '0' COMMENT '警报级别，0为最低',\n" +
                "  `message` text,\n" +
                "  PRIMARY KEY (`id`)\n" +
                ") ENGINE=MyISAM DEFAULT CHARSET=utf8;";
        handler = new DatabaseHandler();
        handler.execute(createTable, new Object[]{});
    }

    @Override
    public void writeLog(String message, int level) {
        try {
            handler.insert(new LogMessage(message, level), false);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void writeLog(String message, Throwable e, int level) {
        try {
            handler.insert(new LogMessage(message + "\n" + getMessage(e), level), false);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void writeLog(Throwable e, int level) {
        try {
            handler.insert(new LogMessage(getMessage(e), level), false);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 返回异常的详细信息
     *
     * @param e
     * @return
     */
    public static String getMessage(Throwable e) {
        return e.getMessage();
    }


    @Override
    public void close() {
        handler.close();
    }
}
