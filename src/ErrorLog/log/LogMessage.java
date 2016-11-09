package ErrorLog.log;

import Database.ReflectStuff.Column;
import Database.ReflectStuff.Table;

/**
 * Created by root on 16-10-26.
 */
@Table(value = "tb_test_log")
public class LogMessage {
    @Column(isIndex = true)
    public int id;
    @Column
    public int level;
    @Column
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
