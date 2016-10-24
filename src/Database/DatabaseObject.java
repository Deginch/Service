package Database;

import java.lang.reflect.Field;

/**
 * Created by root on 16-8-18.
 */
public interface DatabaseObject {

    Field getIndexField() throws NoSuchFieldException;

    String getTableName();
}
