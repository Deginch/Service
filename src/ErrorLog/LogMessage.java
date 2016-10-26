package ErrorLog;

import Database.DatabaseObject;

import java.lang.reflect.Field;

/**
 * Created by root on 16-10-26.
 */
public class LogMessage implements DatabaseObject {
    public int id;
    public int level;
    public String message;
    private static String tableName;

    public LogMessage(String message, int level) {
        this.message = message;
        this.level = level;
    }

    @Override
    public Field getIndexField() throws NoSuchFieldException {
        return this.getClass().getField("id");
    }

    @Override
    public String getTableName() {
        return tableName;
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
