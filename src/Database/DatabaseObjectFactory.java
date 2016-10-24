package Database;

import java.sql.ResultSet;

/**
 * Created by root on 16-10-20.
 */
public interface DatabaseObjectFactory {
    DatabaseObject newDatabaseObject(ResultSet row);
}
