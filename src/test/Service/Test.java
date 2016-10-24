package test.Service;

import Service.Service;
import Tool.ErrorLog;

/**
 * Created by root on 16-8-22.
 */
public class Test extends Service {

    public String a;
    public String b;

    @Override
    public String getTableName() {
        return "mytest";
    }

    @Override
    protected void start() {
        ErrorLog.writeLog("test start");
    }

    @Override
    protected void stop() {
        ErrorLog.writeLog("test stop");
    }

}
