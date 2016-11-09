package Database;

import Database.ReflectStuff.AnnotationCollector;
import Database.ReflectStuff.Column;
import Database.ReflectStuff.ReflectUtil;
import Database.ReflectStuff.Table;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * 用于处理一些数据库对象的操作
 * Created by root on 16-11-9.
 */
public class TableUtil {
    /**
     * 返回对象表名，如果该对象继承了TableInterface，则返回该接口定义的表名，否则返回注解表名
     * @param bean
     * @return
     */
    public static String getTableName(Object bean){
        if(bean instanceof TableInterface){
            return ((TableInterface)bean).getTableName();
        }else {
            return getTableName(bean.getClass());
        }
    }

    /**
     * 返回一个类的注解表名
     *
     * @param clazz
     * @return
     */
    public static String getTableName(Class clazz) {
        if (clazz.isAnnotationPresent(Table.class)) {
            Table table = (Table) clazz.getAnnotation(Table.class);
            return table.value();
        }
        throw new NoSuchElementException("该类无表名注解");
    }

    /**
     * 返回所有注解可以查询的属性
     *
     * @param clazz
     * @return
     */
    public static HashMap<Field, Column> getAllSelectedOrIndexField(Class clazz) {
        return ReflectUtil.getFieldWithAnnotation(clazz, Column.class, new AnnotationCollector() {
            @Override
            public <T extends Annotation> boolean collect(T annotation) {
                return ((Column) annotation).select()||((Column) annotation).isIndex();
            }
        });
    }

    /**
     * 返回所有注解可以查询的属性
     *
     * @param clazz
     * @return
     */
    public static HashMap<Field, Column> getAllUpdateOrIndexField(Class clazz) {
        return ReflectUtil.getFieldWithAnnotation(clazz, Column.class, new AnnotationCollector() {
            @Override
            public <T extends Annotation> boolean collect(T annotation) {
                return ((Column) annotation).update()||((Column) annotation).isIndex();
            }
        });
    }

    /**
     * 返回所有注解可以查询的属性
     *
     * @param clazz
     * @return
     */
    public static HashMap<Field, Column> getAllInsertOrIndexField(Class clazz) {
        return ReflectUtil.getFieldWithAnnotation(clazz, Column.class, new AnnotationCollector() {
            @Override
            public <T extends Annotation> boolean collect(T annotation) {
                return ((Column) annotation).insert()||((Column) annotation).isIndex();
            }
        });
    }

    /**
     * 获取主键
     *
     * @param clazz
     * @return
     */
    public static Field getIndexField(Class clazz) {
        Field index = ReflectUtil.getFieldWithAnnotation(clazz, Column.class, new AnnotationCollector() {
            @Override
            public <T extends Annotation> boolean collect(T annotation) {
                return ((Column) annotation).isIndex();
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
        Annotation annotation = clazz.getAnnotation(Table.class);
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
        Column old = field.getAnnotation(Column.class);
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
        Column old = ReflectUtil.getDeclaredField(clazz,fieldName).getAnnotation(Column.class);
        Map<String, Object> map = ReflectUtil.getAnnotationMemberValues(old);
        map.put("isIndex", isIndex);
        map.put("select", select);
        map.put("update", update);
        map.put("insert", insert);
    }

    /**
     * 返回字段对应数据库的字段类型名字，以便于创建字段
     * @param field
     * @return
     */
    public static String getFieldTypeForMysql(Field field){
        if(field.getType().equals(java.util.Date.class)){
            return "datetime";
        }else {
            return field.getType().getName();
        }
    }
}
