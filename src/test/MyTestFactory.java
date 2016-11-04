package test;

import ErrorLog.ErrorLog;
import Service.Service;
import Service.ServiceFactory;

/**
 * Created by sheldon on 16-11-4.
 */
public class MyTestFactory extends ServiceFactory {
    @Override
    public Service getService(int type) {
        return new Mytest();
    }

    @Override
    public Service getDefaultService() {
        return new Mytest();
    }

    @Override
    public String getServiceName() {
        return "mytest";
    }

    @Override
    public int getLogTypes() {
        return ErrorLog.DEBUG_LOG;
    }

    @Override
    public Class getServiceClass() {
        return Mytest.class;
    }
}
