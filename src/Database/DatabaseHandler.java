package Database;

import ErrorLog.ErrorLog;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by root on 16-8-18.
 */
public class DatabaseHandler {

    private DBHelper dbHelper;

    public DatabaseHandler() throws SQLException {
        dbHelper = new DBHelper();
    }

    public DatabaseHandler(DBHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    public void reOpen() {
        dbHelper.reOpen();
    }

    /**
     * 蒋对象的最新信息更新到数据库
     *
     * @param databaseObject
     */
    public int update(DatabaseObject databaseObject) throws SQLException {
        try {
            Field indexField = databaseObject.getIndexField();
            Field[] fields = databaseObject.getClass().getFields();
            StringBuffer sqlBuffer = new StringBuffer();
            List<Object> params = new ArrayList<>();
            sqlBuffer.append("update ").append(databaseObject.getTableName()).append(" set ");
            for (Field field : fields) {
                if (!field.equals(indexField)) {
                    sqlBuffer.append(field.getName()).append("=? ");
                    sqlBuffer.append(",");
                    params.add(field.get(databaseObject));
                }
            }
            sqlBuffer.delete(sqlBuffer.length() - 1, sqlBuffer.length()).append(" where ").append(indexField.getName()).append("=?;");
            params.add(indexField.get(databaseObject));
            return dbHelper.execute(sqlBuffer.toString(), params.toArray());
        } catch (IllegalAccessException e) {
            ErrorLog.writeLog(e);
        } catch (NoSuchFieldException e) {
            ErrorLog.writeLog(e);
        }
        return -1;
    }

    /**
     * 从数据库删除指定对象
     *
     * @param databaseObject
     */
    public int delete(DatabaseObject databaseObject) throws SQLException {
        try {
            Field indexField = databaseObject.getIndexField();
            StringBuffer sqlBuffer = new StringBuffer();
            List<Object> params = new ArrayList<>();
            sqlBuffer.append("delete from ").append(databaseObject.getTableName()).append(" where ").append(indexField.getName()).append(" =?;");
            params.add(indexField.get(databaseObject));
            return dbHelper.execute(sqlBuffer.toString(), params.toArray());
        } catch (IllegalAccessException e) {
            ErrorLog.writeLog(e);
        } catch (NoSuchFieldException e) {
            ErrorLog.writeLog(e);
        }
        return -1;

    }

    /**
     * 将对象插入数据库
     *
     * @param databaseObject
     * @param insertIndex    是否插入主键
     */
    public int insert(DatabaseObject databaseObject, boolean insertIndex) throws SQLException {
        Field indexField = null;
        try {
            indexField = databaseObject.getIndexField();
            Field[] fields = databaseObject.getClass().getFields();
            StringBuffer sqlBuffer = new StringBuffer();
            StringBuffer valuesBuffer = new StringBuffer();
            List<Object> params = new ArrayList<>();
            sqlBuffer.append("insert into ").append(databaseObject.getTableName()).append(" (");
            valuesBuffer.append(" (");
            for (Field field : fields) {
                if (!field.equals(indexField) || insertIndex) {
                    sqlBuffer.append(field.getName());
                    sqlBuffer.append(",");
                    params.add(field.get(databaseObject));
                    valuesBuffer.append("?");
                    valuesBuffer.append(",");
                }
            }
            valuesBuffer.delete(valuesBuffer.length() - 1, valuesBuffer.length()).append(")");
            sqlBuffer.delete(sqlBuffer.length() - 1, sqlBuffer.length()).append(")").append("values").append(valuesBuffer);
            return dbHelper.execute(sqlBuffer.toString(), params.toArray());
        } catch (IllegalAccessException e) {
            ErrorLog.writeLog(e);
        } catch (NoSuchFieldException e) {
            ErrorLog.writeLog(e);
        }
        return -1;
    }

    /**
     * 插入记录，如果记录存在则更新
     *
     * @param databaseObject
     */
    public int insertDuplicateUpdate(DatabaseObject databaseObject) throws SQLException {
        Field indexField = null;
        try {
            indexField = databaseObject.getIndexField();
            Field[] fields = databaseObject.getClass().getFields();
            StringBuffer insertBuffer = new StringBuffer();
            StringBuffer valuesBuffer = new StringBuffer();
            List<Object> params = new ArrayList<>();
            insertBuffer.append("insert into ").append(databaseObject.getTableName()).append(" (");
            valuesBuffer.append(" (");
            for (Field field : fields) {
                insertBuffer.append(field.getName());
                insertBuffer.append(",");
                params.add(field.get(databaseObject));
                valuesBuffer.append("?");
                valuesBuffer.append(",");
            }
            valuesBuffer.delete(valuesBuffer.length() - 1, valuesBuffer.length()).append(")");
            insertBuffer.delete(insertBuffer.length() - 1, insertBuffer.length()).append(")").append("values").append(valuesBuffer);
            insertBuffer.append(" on duplicate key update ");
            for (Field field : fields) {
                if (!field.equals(indexField)) {
                    insertBuffer.append(field.getName()).append("=?").append(",");
                    params.add(field.get(databaseObject));
                }
            }
            insertBuffer.delete(insertBuffer.length() - 1, insertBuffer.length()).append(";");
            return dbHelper.execute(insertBuffer.toString(), params.toArray());
        } catch (IllegalAccessException e) {
            ErrorLog.writeLog(e);
        } catch (NoSuchFieldException e) {
            ErrorLog.writeLog(e);
        }
        return -1;
    }

    /**
     * 一次插入多个对象
     *
     * @param insertIndex
     */
    public int insert(List<?> strlist, boolean insertIndex) throws SQLException {
        List<DatabaseObject> list = (List<DatabaseObject>) strlist;
        if (list.size() < 1) {
            return -1;
        }
        Field indexField = null;
        try {
            indexField = list.get(0).getIndexField();
            Field[] fields = list.get(0).getClass().getFields();
            StringBuffer sqlBuffer = new StringBuffer();
            StringBuffer valuesBuffer = new StringBuffer();
            List<Object> params = new ArrayList<>();
            sqlBuffer.append("insert into ").append(list.get(0).getTableName()).append(" (");
            for (Field field : fields) {
                if (!field.equals(indexField) || insertIndex) {
                    sqlBuffer.append(field.getName());
                    sqlBuffer.append(",");
                }
            }
            for (DatabaseObject sqlObject : list) {
                valuesBuffer.append(" (");
                for (Field field : fields) {
                    if (!field.equals(indexField) || insertIndex) {
                        valuesBuffer.append("?");
                        valuesBuffer.append(",");
                        params.add(field.get(sqlObject));
                    }
                }
                valuesBuffer.delete(valuesBuffer.length() - 1, valuesBuffer.length()).append(")").append(",");
            }
            valuesBuffer.delete(valuesBuffer.length() - 1, valuesBuffer.length());
            sqlBuffer.delete(sqlBuffer.length() - 1, sqlBuffer.length()).append(")").append("values").append(valuesBuffer);
            return dbHelper.execute(sqlBuffer.toString(), params.toArray());
        } catch (IllegalAccessException e) {
            ErrorLog.writeLog(e);
        } catch (NoSuchFieldException e) {
            ErrorLog.writeLog(e);
        }
        return -1;
    }

    /**
     * 从数据库获取最新值
     *
     * @param bean
     */
    public void select(DatabaseObject bean) throws SQLException {
        try {
            Field field = bean.getIndexField();
            field.setAccessible(true);
            String sql = "select * from " + bean.getTableName() + " where " + field.getName() + "=?";
            dbHelper.queryMap(sql, new Object[]{field.get(bean)}, new ResultMapper() {
                Field[] fields = bean.getClass().getFields();

                @Override
                public boolean map(ResultSet row) throws SQLException {
                    fillObject(row, fields, bean);
                    return true;
                }

                @Override
                public Object result() {
                    return null;
                }
            });
        } catch (IllegalAccessException e) {
            ErrorLog.writeLog(e);
        } catch (NoSuchFieldException e) {
            ErrorLog.writeLog(e);
        }
    }

    /**
     * 根据sql查询数据返回指定类对象
     *
     * @param clazz
     * @param sql
     * @param params
     * @return
     */
    public List queryList(Class<?> clazz, String sql, Object[] params) throws SQLException {
        return (List) dbHelper.queryMap(sql, params, new ResultMapper() {
            List<Object> list = new LinkedList<>();
            Field[] fields = clazz.getFields();

            @Override
            public boolean map(ResultSet row) throws SQLException {
                try {
                    Object bean = clazz.newInstance();
                    fillObject(row, fields, bean);
                    list.add(bean);
                } catch (InstantiationException e) {
                    ErrorLog.writeLog(e);
                    return false;
                } catch (IllegalAccessException e) {
                    ErrorLog.writeLog(e);
                    return false;
                }
                return true;
            }

            @Override
            public Object result() {
                return list;
            }
        });
    }

    public Hashtable queryHashTable(String sql, Object[] params, DatabaseObjectFactory factory) throws SQLException {
        return (Hashtable) dbHelper.queryMap(sql, params, new ResultMapper() {
            Hashtable<Object, Object> hashMap = new Hashtable<>();
            Field[] fields;

            @Override
            public boolean map(ResultSet row) throws SQLException {
                DatabaseObject bean = factory.newDatabaseObject(row);
                if (fields == null) {
                    fields = bean.getClass().getFields();
                }
                if (!fillObject(row, fields, bean)) {
                    return false;
                }
                try {
                    Object index = row.getObject(bean.getIndexField().getName());
                    hashMap.put(index, bean);
                } catch (NoSuchFieldException e) {
                    ErrorLog.writeLog(e);
                    return false;
                }
                return true;
            }

            @Override
            public Object result() {
                return hashMap;
            }
        });
    }

    /**
     * 查询一个数据
     *
     * @param sql
     * @param params
     * @return
     */
    public Object queryOneData(String sql, Object[] params) throws SQLException {
        return dbHelper.queryMap(sql, params, new ResultMapper() {
            Object value;

            @Override
            public boolean map(ResultSet row) throws SQLException {
                value = row.getObject(1);
                return true;
            }

            @Override
            public Object result() {
                return value;
            }
        });
    }

    public int execute(String sql, Object[] params) throws SQLException {
        return dbHelper.execute(sql,params);
    }

    /**
     * 向指定类填充指定数据
     *
     * @param row
     * @param fields
     * @param bean
     * @return
     * @throws SQLException
     */
    private boolean fillObject(ResultSet row, Field[] fields, Object bean) throws SQLException {
        try {
            for (Field field : fields) {
                Object value = row.getObject(field.getName());
                field.setAccessible(true);
                field.set(bean, value);
            }
        } catch (IllegalAccessException e) {
            ErrorLog.writeLog(e);
            return false;
        }
        return true;
    }

    /**
     * 退出并释放资源
     */
    public void close() {
        if (dbHelper != null) {
            dbHelper.close();
            dbHelper = null;
        }
    }



}
