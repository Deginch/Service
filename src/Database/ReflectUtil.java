package Database;

import sun.management.MethodInfo;
import sun.reflect.FieldInfo;
import test.Mytest;

import javax.xml.crypto.Data;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.*;

/**
 * Created by sheldon on 16-11-3.
 */
public class ReflectUtil {

    /**
     * 获取所有属性，包括父类
     *
     * @param clazz
     * @return
     */
    public static List<Field> getAllDeclaredFields(Class clazz) {
        Field[] fields = clazz.getDeclaredFields();
        List<Field> list = new LinkedList<>();
        for (Field field : fields) {
            field.setAccessible(true);
            list.add(field);
        }
        if (!clazz.equals(Object.class)&&!clazz.getSuperclass().equals(Object.class)) {
            list.addAll(getAllDeclaredFields(clazz.getSuperclass()));
        }
        return list;
    }


    public static Field getDeclaredField(Class clazz,String fieldName){
        if(clazz.equals(Object.class)){
            return null;
        }
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            if(field.getName().equals(fieldName)){
                return field;
            }
        }
        return getDeclaredField(clazz.getSuperclass(),fieldName);
    }

    /**
     * 获取带有对应注解的属性，包括父类
     *
     * @param clazz
     * @param annotationClass
     * @param <T>
     * @return
     */
    public static <T extends Annotation> HashMap<Field, T> getFieldWithAnnotation(Class clazz, Class<T> annotationClass, AnnotationCollector collector) {
        HashMap<Field, T> hashMap = new HashMap<>();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(annotationClass)) {
                T annotation = field.getAnnotation(annotationClass);
                if (collector.collect(annotation)) {
                    field.setAccessible(true);
                    hashMap.put(field, annotation);
                }
            }
        }
        if (!clazz.getSuperclass().equals(Object.class)) {
            hashMap.putAll(getFieldWithAnnotation(clazz.getSuperclass(), annotationClass, collector));
        }
        return hashMap;
    }


    /**
     * 更改注解的内容
     * @param annotation 要改变的注解
     * @param key 要改变的注解的内容的名字
     * @param newValue 新的数据
     * @return 返回数据存在的之前的注解
     */
    public static Object changeAnnotationValue(Annotation annotation, String key, Object newValue) {
        Map<String, Object> memberValues=getAnnotationMemberValues(annotation);
        Object oldValue = memberValues.get(key);
        if (oldValue == null || oldValue.getClass() != newValue.getClass()) {
            throw new IllegalArgumentException();
        }
        memberValues.put(key, newValue);
        return oldValue;
    }

    public static Map<String, Object> getAnnotationMemberValues(Annotation annotation){
        Object handler = Proxy.getInvocationHandler(annotation);
        Field f;
        try {
            f = handler.getClass().getDeclaredField("memberValues");
        } catch (NoSuchFieldException | SecurityException e) {
            throw new IllegalStateException(e);
        }
        f.setAccessible(true);
        Map<String, Object> memberValues;
        try {
            memberValues = (Map<String, Object>) f.get(handler);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
        return memberValues;
    }
}

interface AnnotationCollector {
    <T extends Annotation> boolean collect(T annotation);
}