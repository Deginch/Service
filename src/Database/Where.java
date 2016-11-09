package Database;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by root on 16-11-3.
 */
public class Where {
    private String sentence="";

    private List<Object> params=new LinkedList<>();

    private Where(String sentence,Object ... params){
        this.sentence=sentence;
        this.params.addAll(Arrays.asList(params));
    }

    /**
     * 大于指定值的条件
     * @param field
     * @param param
     * @return
     */
    public static Where bigger(String field,Object param){
        return new Where(field+" > ? ",param);
    }

    public static Where smaller(String field,Object param){
        return new Where(field+" > ? ",param);
    }

    public static Where between(String field,Object start,Object end){
        return new Where(" ("+field+" between ? and ?) ",start,end);
    }

    /**
     * 等于指定值的条件
     * @param field
     * @param param
     * @return
     */
    public static Where isEqual(String field,Object param){
        return new Where(field+" = ? ",param);
    }

    /**
     * 不等于指定值的条件
     * @param field
     * @param param
     * @return
     */
    public static Where notEqual(String field,Object param){
        return new Where(field+" != ? ",param);
    }

    /**
     * 无条件，条件为空
     * @return
     */
    public static Where noWhere(){
        return new Where("");
    }

    /**
     * 两条件与
     * @param where
     * @return
     */
    public Where and(Where where){
        if(isNoWhere()||where.isNoWhere()){
            sentence+=where.sentence;
        }else {
            sentence = " ("+sentence+" and " + where.sentence+") ";
        }
        params.addAll(where.params);
        return this;
    }

    /**
     * 两条件或
     * @param where
     * @return
     */
    public Where or(Where where){
        if(isNoWhere()||where.isNoWhere()){
            sentence+=where.sentence;
        }else {
            sentence = " ("+sentence+" or " + where.sentence+") ";
        }
        params.addAll(where.params);
        return this;
    }

    public String toString(){
        return isNoWhere()?"":" where "+sentence;
    }

    /**
     * 获取条件参数
     * @return
     */
    public List<Object> getParams() {
        return params;
    }

    /**
     * 判断条件是否为空
     * @return
     */
    public boolean isNoWhere(){
        return params.size()==0;
    }
}
