package com.yee.yygh.order.util;

import com.alibaba.fastjson.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class HttpRequestHelper {
    /**
     *
     * @param paramMap
     * @return
     */
    public static Map<String, Object> switchMap(Map<String, String[]> paramMap) {
        Map<String, Object> resultMap = new HashMap<>();
        for (Map.Entry<String, String[]> param : paramMap.entrySet()) {
            resultMap.put(param.getKey(), param.getValue()[0]);
        }
        return resultMap;
    }




    /**
     * 获取时间戳
     * @return
     */
    public static long getTimestamp() {
        return new Date().getTime();
    }

    /**
     * 封装同步请求
     */
    public static JSONObject sendRequest(Map<String, Object> paramMap, String url){
        String result = "";
        try {
            //封装post参数
            StringBuilder postdata = new StringBuilder();
            for (Map.Entry<String, Object> param : paramMap.entrySet()) {
                postdata.append(param.getKey()).append("=")
                        .append(param.getValue()).append("&");
            }

            byte[] reqData = postdata.toString().getBytes("utf-8");
            byte[] respdata = HttpUtil.doPost(url,reqData);
            result = new String(respdata);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return JSONObject.parseObject(result);
    }
}

