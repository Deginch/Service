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

    public static Where bigger(String field,Object param){
        return new Where(field+" > ? ",param);
    }

    public static Where smaller(String field,Object param){
        return new Where(field+" > ? ",param);
    }

    public static Where between(String field,Object start,Object end){
        return new Where(" ("+field+" between ? and ?) ",start,end);
    }

    public static Where isEqual(String field,Object param){
        return new Where(field+" = ? ",param);
    }

    public static Where notEqual(String field,Object param){
        return new Where(field+" != ? ",param);
    }

    public static Where noWhere(){
        return new Where("");
    }

    public Where and(Where where){
        if(isNoWhere()||where.isNoWhere()){
            sentence+=where.sentence;
        }else {
            sentence = " ("+sentence+" and " + where.sentence+") ";
        }
        params.addAll(where.params);
        return this;
    }

    public Where or(Where where){
        if(isNoWhere()||where.isNoWhere()){
            sentence+=where.sentence;
        }else {
            sentence = " ("+sentence+" or " + where.sentence+") ";
        }
        params.addAll(where.params);
        return this;
    }

    public String getWhereSentence(){
        return isNoWhere()?"":" where "+sentence;
    }

    public List<Object> getParams() {
        return params;
    }

    public boolean isNoWhere(){
        return params.size()==0;
    }
}
