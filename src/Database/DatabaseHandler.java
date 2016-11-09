package Database;

import Database.ReflectStuff.Column;
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
     * @param bean
     */
    public int update(Object bean) throws SQLException {
        try {
            Where where = Where.noWhere();
            StringBuffer sqlBuffer = new StringBuffer();
            List<Object> params = new ArrayList<>();
            sqlBuffer.append("update ").append(TableUtil.getTableName(bean)).append(" set ");
            HashMap<Field, Column> fields = TableUtil.getAllUpdateOrIndexField(bean.getClass());
            for (Map.Entry<Field, Column> entry : fields.entrySet()) {
                if (entry.getValue().isIndex()) {
                    where.and(Where.isEqual(entry.getKey().getName(), entry.getKey().get(bean)));
                } else {
                    sqlBuffer.append(entry.getKey().getName()).append("=? ");
                    sqlBuffer.append(",");
                    params.add(entry.getKey().get(bean));
                }
            }
            sqlBuffer.delete(sqlBuffer.length() - 1, sqlBuffer.length()).append(where.toString());
            params.addAll(where.getParams());
            return dbHelper.execute(sqlBuffer.toString(), params.toArray());
        } catch (IllegalAccessException e) {
            ErrorLog.writeLog(e);
        }
        return -1;
    }

    /**
     * 从数据库删除指定对象
     *
     * @param bean
     */
    public int delete(Objects bean) throws SQLException {
        try {
            Field indexField = TableUtil.getIndexField(bean.getClass());
            Where where = Where.isEqual(indexField.getName(), indexField.get(bean));
            StringBuffer sqlBuffer = new StringBuffer();
            List<Object> params = new ArrayList<>();
            sqlBuffer.append("delete from ").append(TableUtil.getTableName(bean)).append(where.toString());
            params.addAll(where.getParams());
            return dbHelper.execute(sqlBuffer.toString(), params.toArray());
        } catch (IllegalAccessException e) {
            ErrorLog.writeLog(e);
        }
        return -1;

    }

    /**
     * 将对象插入数据库
     *
     * @param bean
     * @param insertIndex 是否插入主键
     */
    public int insert(Object bean, boolean insertIndex) throws SQLException {
        try {
            HashMap<Field, Column> fields = TableUtil.getAllInsertOrIndexField(bean.getClass());
            StringBuffer sqlBuffer = new StringBuffer();
            StringBuffer valuesBuffer = new StringBuffer();
            List<Object> params = new ArrayList<>();
            sqlBuffer.append("insert into ").append(TableUtil.getTableName(bean)).append(" (");
            valuesBuffer.append(" (");
            for (Map.Entry<Field, Column> entry : fields.entrySet()) {
                if (!entry.getValue().isIndex() || insertIndex) {
                    sqlBuffer.append(entry.getKey().getName());
                    sqlBuffer.append(",");
                    params.add(entry.getKey().get(bean));
                    valuesBuffer.append("?");
                    valuesBuffer.append(",");
                }
            }
            valuesBuffer.delete(valuesBuffer.length() - 1, valuesBuffer.length()).append(")");
            sqlBuffer.delete(sqlBuffer.length() - 1, sqlBuffer.length()).append(")").append("values").append(valuesBuffer);
            return dbHelper.execute(sqlBuffer.toString(), params.toArray());
        } catch (IllegalAccessException e) {
            ErrorLog.writeLog(e);
        }
        return -1;
    }


    /**
     * 插入记录，如果记录存在则更新
     *
     * @param bean
     */
    public int insertDuplicateUpdate(Object bean) throws SQLException {
        Field indexField = null;
        try {
            HashMap<Field, Column> fields = TableUtil.getAllInsertOrIndexField(bean.getClass());
            StringBuffer insertBuffer = new StringBuffer();
            StringBuffer valuesBuffer = new StringBuffer();
            List<Object> params = new ArrayList<>();
            insertBuffer.append("insert into ").append(TableUtil.getTableName(bean)).append(" (");
            valuesBuffer.append(" (");
            for (Map.Entry<Field, Column> entry : fields.entrySet()) {
                insertBuffer.append(entry.getKey().getName());
                insertBuffer.append(",");
                params.add(entry.getKey().get(bean));
                valuesBuffer.append("?");
                valuesBuffer.append(",");
            }
            valuesBuffer.delete(valuesBuffer.length() - 1, valuesBuffer.length()).append(")");
            insertBuffer.delete(insertBuffer.length() - 1, insertBuffer.length()).append(")").append("values").append(valuesBuffer);
            insertBuffer.append(" on duplicate key update ");
            for (Map.Entry<Field, Column> entry : fields.entrySet()) {
                if (!entry.getValue().isIndex()) {
                    insertBuffer.append(entry.getKey().getName()).append("=?").append(",");
                    params.add(entry.getKey().get(bean));
                }
            }
            insertBuffer.delete(insertBuffer.length() - 1, insertBuffer.length()).append(";");
            return dbHelper.execute(insertBuffer.toString(), params.toArray());
        } catch (IllegalAccessException e) {
            ErrorLog.writeLog(e);
        }
        return -1;
    }

    /**
     * 一次插入多个对象
     *
     * @param insertIndex 是否插入主键index
     */
    public int insert(List<?> list,boolean insertIndex) throws SQLException {
        if (list.size() < 1) {
            return -1;
        }
        try {
            HashMap<Field, Column> fields = TableUtil.getAllInsertOrIndexField(list.get(0).getClass());
            StringBuffer sqlBuffer = new StringBuffer();
            StringBuffer valuesBuffer = new StringBuffer();
            List<Object> params = new ArrayList<>();
            sqlBuffer.append("insert into ").append(TableUtil.getTableName(list.get(0))).append(" (");
            for (Map.Entry<Field, Column> entry : fields.entrySet()) {
                if (!entry.getValue().isIndex() || insertIndex) {
                    sqlBuffer.append(entry.getKey().getName());
                    sqlBuffer.append(",");
                }
            }
            for (Object bean : list) {
                valuesBuffer.append(" (");
                for (Map.Entry<Field, Column> entry : fields.entrySet()) {
                    if (!entry.getValue().isIndex() || insertIndex) {
                        valuesBuffer.append("?");
                        valuesBuffer.append(",");
                        params.add(entry.getKey().get(bean));
                    }
                }
                valuesBuffer.delete(valuesBuffer.length() - 1, valuesBuffer.length()).append(")").append(",");
            }
            valuesBuffer.delete(valuesBuffer.length() - 1, valuesBuffer.length());
            sqlBuffer.delete(sqlBuffer.length() - 1, sqlBuffer.length()).append(")").append("values").append(valuesBuffer);
            return dbHelper.execute(sqlBuffer.toString(), params.toArray());
        } catch (IllegalAccessException e) {
            ErrorLog.writeLog(e);
        }
        return -1;
    }

    /**
     * 从数据库获取最新值
     *
     * @param bean
     */
    public void refresh(Object bean) throws SQLException {
        try {
            Field index = TableUtil.getIndexField(bean.getClass());
            Where where=Where.isEqual(index.getName(),index.get(bean));
            HashMap<Field, Column> fieldHashMap = TableUtil.getAllSelectedOrIndexField(bean.getClass());
            StringBuffer sqlBuffer = new StringBuffer("select ");
            for (Map.Entry<Field, Column> entry : fieldHashMap.entrySet()) {
                sqlBuffer.append(entry.getKey().getName());
                sqlBuffer.append(",");
            }
            sqlBuffer.delete(sqlBuffer.length() - 1, sqlBuffer.length());
            sqlBuffer.append(" from ").append(TableUtil.getTableName(bean)).append(where);

            dbHelper.queryMap(sqlBuffer.toString(),where.getParams().toArray() , new ResultMapper() {
                Set<Field> fields = TableUtil.getAllUpdateOrIndexField(bean.getClass()).keySet();

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
        }
    }

    /**
     * 根据sql查询数据返回指定类对象
     */
    public List queryList(Factory factory, Where where) throws SQLException {
        HashMap<Field, Column> fieldHashMap = TableUtil.getAllSelectedOrIndexField(factory.getInstanceClass());
        StringBuffer sqlBuffer = new StringBuffer("select ");
        for (Map.Entry<Field, Column> entry : fieldHashMap.entrySet()) {
            sqlBuffer.append(entry.getKey().getName());
            sqlBuffer.append(",");
        }
        sqlBuffer.delete(sqlBuffer.length() - 1, sqlBuffer.length());
        sqlBuffer.append(" from ").append(TableUtil.getTableName(factory.getInstanceClass())).append(where);

        return (List) dbHelper.queryMap(sqlBuffer.toString(), where.getParams().toArray(), new ResultMapper() {
            List<Object> list = new LinkedList<>();
            Set<Field> fields = fieldHashMap.keySet();

            @Override
            public boolean map(ResultSet row) throws SQLException {
                Object bean = factory.newInstance(row);
                if (fillObject(row, fields, bean)) {
                    list.add(bean);
                    return true;
                }
                return true;
            }

            @Override
            public Object result() {
                return list;
            }
        });
    }

    /**
     * 根据sql查询数据返回指定类对象
     */
    public List queryList(Class clazz, Where where) throws SQLException {
        return queryList(new Factory() {
            @Override
            public Object newInstance(Object... params) {
                try {
                    return clazz.newInstance();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                return new Object();
            }

            @Override
            public Class getInstanceClass() {
                return clazz;
            }
        }, where);
    }

    /**
     * 生成指定类的哈希表，主键为key
     *
     * @param clazz
     * @param where
     * @return
     * @throws SQLException
     */
    public Hashtable queryHashTable(Class clazz, Where where) throws SQLException {
        return queryHashTable(new Factory() {
            @Override
            public Object newInstance(Object... params) {
                try {
                    return clazz.newInstance();
                } catch (InstantiationException e) {
                    ErrorLog.writeLog(e);
                } catch (IllegalAccessException e) {
                    ErrorLog.writeLog(e);
                }
                return new Object();
            }

            @Override
            public Class getInstanceClass() {
                return clazz;
            }
        }, where);
    }

    /**
     * 根据指定的工厂生对对应的哈希表，表头为主键
     *
     * @param factory
     * @param where
     * @return
     * @throws SQLException
     */
    public Hashtable queryHashTable(Factory factory, Where where) throws SQLException {
        HashMap<Field, Column> fieldHashMap = TableUtil.getAllSelectedOrIndexField(factory.getInstanceClass());
        StringBuffer sqlBuffer = new StringBuffer("select ");
        for (Map.Entry<Field, Column> entry : fieldHashMap.entrySet()) {
            sqlBuffer.append(entry.getKey().getName());
            sqlBuffer.append(",");
        }
        sqlBuffer.delete(sqlBuffer.length() - 1, sqlBuffer.length());
        sqlBuffer.append(" from ").append(TableUtil.getTableName(factory.getInstanceClass())).append(where);
        return (Hashtable) dbHelper.queryMap(sqlBuffer.toString(), where.getParams().toArray(), new ResultMapper() {
            Hashtable<Object, Object> hashMap = new Hashtable<>();
            Set<Field> fields = fieldHashMap.keySet();
            Field indexFiled = TableUtil.getIndexField(factory.getInstanceClass());

            @Override
            public boolean map(ResultSet row) throws SQLException {
                Object bean = factory.newInstance(row);
                if (!fillObject(row, fields, bean)) {
                    return false;
                }
                Object index = row.getObject(indexFiled.getName());
                hashMap.put(index, bean);
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
        return dbHelper.execute(sql, params);
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
    private boolean fillObject(ResultSet row, Set<Field> fields, Object bean) throws SQLException {
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
