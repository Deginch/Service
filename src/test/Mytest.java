package test;

import Database.*;
import Service.Service;

/**
 * Created by sheldon on 16-11-4.
 */
@Database("mytest")
public class Mytest extends Service{
    @DatabaseField
    public String a;
    @DatabaseField
    public String b;
    @Override
    protected void start() {

    }

    @Override
    protected void stop() {

    }
}
