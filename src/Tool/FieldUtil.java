package Tool;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 16-8-18.
 */
public class FieldUtil {

    /**
     * 不断递归直到找到此属性
     * @param clazz
     * @param fieldName
     * @return
     * @throws NoSuchFieldException
     */
    public static Field getDeclaredField(Class<?> clazz,String fieldName) throws NoSuchFieldException {
        if(Object.class.equals(clazz)){
            throw new NoSuchFieldException();
        }
        try {
            Field field= clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field;
        } catch (NoSuchFieldException e) {
            return getDeclaredField(clazz.getSuperclass(),fieldName);
        }
    }


    /**
     * 获取对象所有的字段，包括其所有继承的父类的字段
     *
     * @param clazz
     * @return
     */
    public static List<Field> getDeclaredFields(Class<?> clazz) {
        List<Field> fieldLinkedList = new ArrayList<>();
        if (Object.class.equals(clazz)) {
            return fieldLinkedList;
        }
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            fieldLinkedList.add(field);
        }
        fieldLinkedList.addAll(getDeclaredFields(clazz.getSuperclass()));
        return fieldLinkedList;
    }
}
