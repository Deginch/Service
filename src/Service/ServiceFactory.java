package Service;


import Database.DatabaseObject;
import Database.DatabaseObjectFactory;
import Tool.ErrorLog;
import com.sun.xml.internal.ws.api.addressing.WSEndpointReference;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/**
 * Created by sheldon on 16-7-19.
 */
public abstract class ServiceFactory implements DatabaseObjectFactory {
    /**
     * get Service instance by type
     * @param type
     * @return
     */
    public abstract Service getService(int type);

    /**
     * 返回一个服务实例
     * @return
     */
    public abstract Service getDefaultService();

    /**
     * 返回服务名称
     * @return
     */
    public abstract String getServiceName();

    /**
     * 返回服务类
     * @return
     */
    public abstract Class getServiceClass();

    @Override
    public DatabaseObject newDatabaseObject(ResultSet row) {
        try {
            return getService(row.getInt("service_type"));
        } catch (SQLException e) {
        }
        return getDefaultService();
    }


}