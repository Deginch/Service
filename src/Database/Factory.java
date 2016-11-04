package Database;

import java.sql.ResultSet;

/**
 * Created by root on 16-10-20.
 */
public interface Factory {
    Object newInstance(Object ... params);
    Class getInstanceClass();
}
