package com.zhengdao.zqb.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;
import java.util.Map;

/**
 * gson工具类
 * Created by lizhiping on 2018/4/8.
 */

public class GsonUtil {

    public static String mapToJson(Map map) {
        if (map == null) {
            return "";
        }
        Gson gson2 = new GsonBuilder().enableComplexMapKeySerialization().create();
        return gson2.toJson(map);
    }

    public static String ListToJson(List datas) {

        if (datas == null) {
            return "";
        }
        Gson gson = new Gson();
        return gson.toJson(datas);
    }

    public static String ObjectToJson(Object data) {
        if (data == null) {
            return "";
        }
        Gson gson = new Gson();
        return gson.toJson(data);
    }

    public static <T> T fromJson(String jsonData, Class<T> type) {
        return new Gson().fromJson(jsonData, type);
    }


}
