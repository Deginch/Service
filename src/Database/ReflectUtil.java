package Database;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
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
        if (!clazz.getSuperclass().equals(Object.class)) {
            list.addAll(getAllDeclaredFields(clazz.getSuperclass()));
        }
        return list;
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
            hashMap.putAll(getFieldWithAnnotation(clazz.getSuperclass(), annotationClass,collector));
        }
        return hashMap;
    }


}

interface AnnotationCollector {
    <T extends Annotation> boolean collect(T annotation);
}