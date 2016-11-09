package ErrorLog.log;

import Database.ReflectStuff.Column;
import Database.ReflectStuff.Table;
import Database.TableInterface;

/**
 * Created by root on 16-10-26.
 */
public class LogMessage implements TableInterface{
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

    @Override
    public String getTableName() {
        return tableName;
    }
}
