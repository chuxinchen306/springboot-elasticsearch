package com.chuxin.springboot.util;

import com.alibaba.fastjson.JSONObject;

import java.util.Map;

/**
 * Created by chenchx on 2018/11/5.
 */
public class MapConvert {
    public static <T> T mapToObject(Map<String,Object> map, Class<T> tClass){
        try {
            String jsonStr = JSONObject.toJSONString(map);
            T obj = JSONObject.parseObject(jsonStr, tClass);
            return  obj;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;

    }

    public static <T> Map<String,Object> objectToMap(T t) {
        try {
            String jsonStr = JSONObject.toJSONString(t);

            JSONObject jsonObject= JSONObject.parseObject(jsonStr);
            return jsonObject;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
