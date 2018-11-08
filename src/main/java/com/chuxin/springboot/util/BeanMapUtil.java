package com.chuxin.springboot.util;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by chenchx on 2018/11/7.
 */
public class BeanMapUtil {
    public static Map<String,Object> objectToMap(Object o){
        Map<String,Object> map = new HashMap<>(16);
        Field[] fields = o.getClass().getDeclaredFields();
        for (Field field : fields){
            field.setAccessible(true);
            try {
                if (field.get(o)!=null){
                    map.put(field.getName(),field.get(o));
                }
            }catch (IllegalAccessException e){
                e.printStackTrace();
            }
        }
        return map;
    }
}
