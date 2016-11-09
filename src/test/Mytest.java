package test;

import Database.ReflectStuff.Column;
import Database.ReflectStuff.Table;
import Service.Service;

/**
 * Created by sheldon on 16-11-4.
 */
@Table("mytest")
public class Mytest extends Service{
    @Column
    public String a;
    @Column
    public String b;
    @Override
    protected void start() {

    }

    @Override
    protected void stop() {

    }
}
