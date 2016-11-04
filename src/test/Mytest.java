package test;

import Database.*;
import Service.Service;

/**
 * Created by sheldon on 16-11-4.
 */
@Database(tableName = "mytest")
public class Mytest extends Service{
    @DatabaseField
    private String a;
    @DatabaseField
    private String b;
    @Override
    protected void start() {

    }

    @Override
    protected void stop() {

    }
}
