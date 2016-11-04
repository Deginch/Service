package Service;


import Database.Factory;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by sheldon on 16-7-19.
 */
public abstract class ServiceFactory implements Factory {
    /**
     * get Service instance by type
     *
     * @param type
     * @return
     */
    public abstract Service getService(int type);

    /**
     * 返回一个服务实例
     *
     * @return
     */
    public abstract Service getDefaultService();

    /**
     * 返回服务名称
     *
     * @return
     */
    public abstract String getServiceName();

    public abstract int getLogTypes();

    /**
     * 返回服务类
     *
     * @return
     */
    public abstract Class getServiceClass();

    @Override
    public Object newInstance(Object... params) {
        try {
            return getService(((ResultSet) (params[0])).getInt("service_type"));
        } catch (SQLException e) {
        }
        return getDefaultService();
    }

    @Override
    public Class getInstanceClass() {
        return getServiceClass();
    }
}
