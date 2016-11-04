package Database;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by root on 16-11-3.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface DatabaseField {
    /**
     * 是否是索引，默认否
     * @return
     */
    boolean isIndex()default false;

    /**
     * 字段是否能查询，默认可以查询
     * @return
     */
    boolean select()default true;

    /**
     * 字段是否能更新,默认不可以更新
     * @return
     */
    boolean update()default false;

    /**
     * 字段是否能插入，默认可以插入
     * @return
     */
    boolean insert()default true;
}
