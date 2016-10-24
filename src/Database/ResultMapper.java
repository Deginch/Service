package Database;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by root on 16-10-20.
 */
public interface ResultMapper {
    boolean map(final ResultSet row) throws SQLException;

    Object result();
}
