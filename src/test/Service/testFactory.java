package test.Service;

import ErrorLog.ErrorLog;
import Service.ServiceFactory;

/**
 * Created by root on 16-8-19.
 */
public class testFactory extends ServiceFactory {
    /**
     * get Service instance by type
     *
     * @param type
     * @return
     */
    @Override
    public Service.Service getService(int type) {
        return null;
    }

    /**
     * 返回一个服务实例
     *
     * @return
     */
    @Override
    public Service.Service getDefaultService() {
        return new Test();
    }

    /**
     * 返回服务名称
     *
     * @return
     */
    @Override
    public String getServiceName() {
        return "test";
    }

    @Override
    public ErrorLog.LogType[] getLogTypes() {
        return null;
    }

    /**
     * 返回服务类
     *
     * @return
     */
    @Override
    public Class getServiceClass() {
        return Test.class;
    }

}
