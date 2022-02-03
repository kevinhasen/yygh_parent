package com.yee.yygh.common.util;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class HttpRequestHelper {

    /**
     * 获取时间戳
     * @return
     */
    public static long getTimestamp() {
        return new Date().getTime();
    }

    /**
     * 旧版
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
     * 新版
     * @param paramMap  传进来请求转为需要的对象
     * @return
     */
//    public static<T> T switchMap(Map<String, String[]> paramMap, Class<T> obj) {
//
//        //转为普通map
//        Map<String, Object> resultMap = new HashMap<>();
//        for (Map.Entry<String, String[]> param : paramMap.entrySet()) {
//            resultMap.put(param.getKey(), param.getValue()[0]);
//        }
//        //转为对象
//        String toJSONString  = JSONObject.toJSONString(resultMap);
//        return JSONObject.parseObject(toJSONString, obj);
//    }


    }
