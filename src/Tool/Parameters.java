package Tool;
import java.util.*;
/**
 * Created by sheldon on 16-10-17.
 */
public class Parameters {

    HashMap<String,String> parameters=new HashMap<>();

    public Parameters(String[] args){
        Iterator<String> iterator=new Iterator<String>() {
            int i=0;
            @Override
            public boolean hasNext() {
                return i<args.length;
            }

            @Override
            public String next() {
                return args[i++];
            }
        };
        while (iterator.hasNext()){
            String parameter=iterator.next();
            if(parameter.charAt(0)=='-'){
                if(iterator.hasNext()){
                    parameters.put(parameter.substring(1),iterator.next());
                }
            }
        }
    }

    /**
     * 获取指定key的参数
     * @param key
     * @return
     */
    public String getParameter(String key,String defaultParameter){
        return parameters.containsKey(key)?parameters.get(key):defaultParameter;
    }

    /**
     * 获取指定key的参数
     * @param key
     * @return
     */
    public String getParameter(String key){
        return parameters.get(key);
    }

    public boolean hasParameter(String key){
        return parameters.containsKey(key);
    }
}
