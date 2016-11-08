package Database;

import ErrorLog.ErrorLog;

import java.lang.annotation.Annotation;
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
            sqlBuffer.append("update ").append(getTableName(bean.getClass())).append(" set ");
            HashMap<Field, DatabaseField> fields = getAllUpdateField(bean.getClass());
            for (Map.Entry<Field, DatabaseField> entry : fields.entrySet()) {
                if (entry.getValue().isIndex()) {
                    where.and(Where.isEqual(entry.getKey().getName(), entry.getKey().get(bean)));
                } else {
                    sqlBuffer.append(entry.getKey().getName()).append("=? ");
                    sqlBuffer.append(",");
                    params.add(entry.getKey().get(bean));
                }
            }
            sqlBuffer.delete(sqlBuffer.length() - 1, sqlBuffer.length()).append(where.getWhereSentence());
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
            Field indexField = getIndexField(bean.getClass());
            Where where = Where.isEqual(indexField.getName(), indexField.get(bean));
            StringBuffer sqlBuffer = new StringBuffer();
            List<Object> params = new ArrayList<>();
            sqlBuffer.append("delete from ").append(getTableName(bean.getClass())).append(where.getWhereSentence());
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
        Field indexField = null;
        try {
            HashMap<Field, DatabaseField> fields = getAllInsertField(bean.getClass());
            StringBuffer sqlBuffer = new StringBuffer();
            StringBuffer valuesBuffer = new StringBuffer();
            List<Object> params = new ArrayList<>();
            sqlBuffer.append("insert into ").append(getTableName(bean.getClass())).append(" (");
            valuesBuffer.append(" (");
            for (Map.Entry<Field, DatabaseField> entry : fields.entrySet()) {
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
            HashMap<Field, DatabaseField> fields = getAllInsertField(bean.getClass());
            StringBuffer insertBuffer = new StringBuffer();
            StringBuffer valuesBuffer = new StringBuffer();
            List<Object> params = new ArrayList<>();
            insertBuffer.append("insert into ").append(getTableName(bean.getClass())).append(" (");
            valuesBuffer.append(" (");
            for (Map.Entry<Field, DatabaseField> entry : fields.entrySet()) {
                insertBuffer.append(entry.getKey().getName());
                insertBuffer.append(",");
                params.add(entry.getKey().get(bean));
                valuesBuffer.append("?");
                valuesBuffer.append(",");
            }
            valuesBuffer.delete(valuesBuffer.length() - 1, valuesBuffer.length()).append(")");
            insertBuffer.delete(insertBuffer.length() - 1, insertBuffer.length()).append(")").append("values").append(valuesBuffer);
            insertBuffer.append(" on duplicate key update ");
            for (Map.Entry<Field, DatabaseField> entry : fields.entrySet()) {
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
     * @param insertIndex
     */
    public int insert(List<?> list, boolean insertIndex) throws SQLException {
        if (list.size() < 1) {
            return -1;
        }
        Field indexField = null;
        try {
            HashMap<Field, DatabaseField> fields = getAllInsertField(list.get(0).getClass());
            StringBuffer sqlBuffer = new StringBuffer();
            StringBuffer valuesBuffer = new StringBuffer();
            List<Object> params = new ArrayList<>();
            sqlBuffer.append("insert into ").append(getTableName(list.get(0).getClass())).append(" (");
            for (Map.Entry<Field, DatabaseField> entry : fields.entrySet()) {
                if (!entry.getValue().isIndex() || insertIndex) {
                    sqlBuffer.append(entry.getKey().getName());
                    sqlBuffer.append(",");
                }
            }
            for (Object bean : list) {
                valuesBuffer.append(" (");
                for (Map.Entry<Field, DatabaseField> entry : fields.entrySet()) {
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
            Field index = getIndexField(bean.getClass());
            String sql = "select * from " + getTableName(bean.getClass()) + " where " + index.getName() + "=?";
            dbHelper.queryMap(sql, new Object[]{index.get(bean)}, new ResultMapper() {
                Set<Field> fields = getAllUpdateField(bean.getClass()).keySet();

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
        HashMap<Field, DatabaseField> fieldHashMap = getAllSelectedField(factory.getInstanceClass());
        StringBuffer sqlBuffer = new StringBuffer("select ");
        for (Map.Entry<Field, DatabaseField> entry : fieldHashMap.entrySet()) {
            sqlBuffer.append(entry.getKey().getName());
            sqlBuffer.append(",");
        }
        sqlBuffer.delete(sqlBuffer.length() - 1, sqlBuffer.length());
        sqlBuffer.append(" from ").append(getTableName(factory.getInstanceClass())).append(where.getWhereSentence());

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
        HashMap<Field, DatabaseField> fieldHashMap = getAllSelectedField(factory.getInstanceClass());
        StringBuffer sqlBuffer = new StringBuffer("select ");
        for (Map.Entry<Field, DatabaseField> entry : fieldHashMap.entrySet()) {
            sqlBuffer.append(entry.getKey().getName());
            sqlBuffer.append(",");
        }
        sqlBuffer.delete(sqlBuffer.length() - 1, sqlBuffer.length());
        sqlBuffer.append(" from ").append(getTableName(factory.getInstanceClass())).append(where.getWhereSentence());
        return (Hashtable) dbHelper.queryMap(sqlBuffer.toString(), where.getParams().toArray(), new ResultMapper() {
            Hashtable<Object, Object> hashMap = new Hashtable<>();
            Set<Field> fields = fieldHashMap.keySet();
            Field indexFiled = getIndexField(factory.getInstanceClass());

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

    /**
     * 返回一个类的表名
     *
     * @param clazz
     * @return
     */
    private String getTableName(Class clazz) {
        if (clazz.isAnnotationPresent(Database.class)) {
            Database database = (Database) clazz.getAnnotation(Database.class);
            return database.value();
        }
        throw new NoSuchElementException("该类无表名注解");
    }

    /**
     * 返回所有注解可以查询的属性
     *
     * @param clazz
     * @return
     */
    private HashMap<Field, DatabaseField> getAllSelectedField(Class clazz) {
        return ReflectUtil.getFieldWithAnnotation(clazz, DatabaseField.class, new AnnotationCollector() {
            @Override
            public <T extends Annotation> boolean collect(T annotation) {
                return ((DatabaseField) annotation).select();
            }
        });
    }

    /**
     * 返回所有注解可以查询的属性
     *
     * @param clazz
     * @return
     */
    private HashMap<Field, DatabaseField> getAllUpdateField(Class clazz) {
        return ReflectUtil.getFieldWithAnnotation(clazz, DatabaseField.class, new AnnotationCollector() {
            @Override
            public <T extends Annotation> boolean collect(T annotation) {
                return ((DatabaseField) annotation).update();
            }
        });
    }

    /**
     * 返回所有注解可以查询的属性
     *
     * @param clazz
     * @return
     */
    private HashMap<Field, DatabaseField> getAllInsertField(Class clazz) {
        return ReflectUtil.getFieldWithAnnotation(clazz, DatabaseField.class, new AnnotationCollector() {
            @Override
            public <T extends Annotation> boolean collect(T annotation) {
                return ((DatabaseField) annotation).insert();
            }
        });
    }

    /**
     * 获取主键
     *
     * @param clazz
     * @return
     */
    private Field getIndexField(Class clazz) {
        Field index = ReflectUtil.getFieldWithAnnotation(clazz, DatabaseField.class, new AnnotationCollector() {
            @Override
            public <T extends Annotation> boolean collect(T annotation) {
                return ((DatabaseField) annotation).insert();
            }
        }).keySet().iterator().next();
        if (index != null) {
            return index;
        } else {
            throw new NoSuchElementException("该类无主键注解");
        }
    }

    /**
     * 改变类的数据库表名
     *
     * @param clazz
     * @param tableName
     * @return 返回之前的旧表名
     */
    public static String changeTableName(Class clazz, String tableName) {
        Annotation annotation = clazz.getAnnotation(Database.class);
        if (annotation == null)
            throw new IllegalArgumentException("该类无表名");
        return (String) ReflectUtil.changeAnnotationValue(annotation, "value", tableName);
    }

    /**
     * 改变属性的数据库表值
     *
     * @param field 必须是getDeclaredField(s)获取的字段，否则失败
     * @param isIndex
     * @param select
     * @param update
     * @param insert
     */
    public static void changeFieldProperty(Field field, boolean isIndex, boolean select, boolean update, boolean insert) {
        DatabaseField old = field.getAnnotation(DatabaseField.class);
        Map<String, Object> map = ReflectUtil.getAnnotationMemberValues(old);
        map.put("isIndex", isIndex);
        map.put("select", select);
        map.put("update", update);
        map.put("insert", insert);
    }

    /**
     * 改变属性的数据库表值
     *@param clazz
     * @param fieldName
     * @param isIndex
     * @param select
     * @param update
     * @param insert
     */
    public static void changeFieldProperty(Class clazz,String fieldName, boolean isIndex, boolean select, boolean update, boolean insert) {
        DatabaseField old = ReflectUtil.getDeclaredField(clazz,fieldName).getAnnotation(DatabaseField.class);
        Map<String, Object> map = ReflectUtil.getAnnotationMemberValues(old);
        map.put("isIndex", isIndex);
        map.put("select", select);
        map.put("update", update);
        map.put("insert", insert);
    }
}
