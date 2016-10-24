package Service;

import Database.DatabaseObject;
import ServiceHandler.ServiceChanged;
import ServiceHandler.ServiceState;
import Tool.ErrorLog;

import java.lang.reflect.Field;

/**
 * Created by degin on 2016/6/30.
 * 基础服务类，凡是服务都要继承此类，实现start，serviceStop，restart等方法，
 * 如果需要在所有服务启动前做一些初始化工作，请继承static void init，在此方法下写初始化方法。
 * 自带id属性，子类无需再设置id值
 */
public abstract class Service implements DatabaseObject {


    public int id;
    public int changed;
    public int state;

    protected volatile boolean isRun = false;

    @Override
    public Field getIndexField() throws NoSuchFieldException {
        return this.getClass().getField("id");
    }

    /**
     * @Date 2016/7/1
     * 初始化方法
     */
    public static void beforeStart() {

    }

    public static void afterStop() {
    }

    /**
     * @Date 2016/6/30
     * 开始运行
     */
    public void serviceStart(){
        isRun=true;
        state=ServiceState.Running.getState();
        changed=ServiceChanged.NoChange.getChanged();
        ErrorLog.writeLog(id+" service start");
        start();
    }


    /**
     * 停止运行
     */
    public void serviceStop(){
        isRun=false;
        state=ServiceState.Sleep.getState();
        ErrorLog.writeLog(id+" service stop");
        stop();
    }


    protected abstract void start();

    protected abstract void stop();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getChanged() {
        return changed;
    }

    public void setChanged(int changed) {
        this.changed = changed;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
}