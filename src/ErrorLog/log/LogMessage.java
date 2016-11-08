package ErrorLog.log;

import Database.*;

/**
 * Created by root on 16-10-26.
 */
@Database(value = "tb_test_log")
public class LogMessage {
    @DatabaseField(isIndex = true)
    public int id;
    @DatabaseField
    public int level;
    @DatabaseField
    public String message;
    private static String tableName;

    public LogMessage(String message, int level) {
        this.message = message;
        this.level = level;
    }
    /**
     * 设置存储的数据库表名
     *
     * @param tableName
     */
    public static void initDatabase(String tableName) {
        LogMessage.tableName = tableName;
    }

}
