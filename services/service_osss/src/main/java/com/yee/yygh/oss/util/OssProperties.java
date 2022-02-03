package com.yee.yygh.oss.util;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * ClassName: ossProperties
 * Description:
 * date: 2022/1/7 11:03
 * 读取配置文件oss属性
 * @author Yee
 * @since JDK 1.8
 */
@Component
public class OssProperties implements InitializingBean {

    @Value("${aliyun.endpoint}")
    private String endpoint;
    @Value("${aliyun.keyid}")
    private String keyid;
    @Value("${aliyun.keysecret}")
    private String keysecret;
    @Value("${aliyun.bucketname}")
    private String bucketname;

    public static String ENDPOINT;
    public static String KEYID;
    public static String KEYSECRET;
    public static String  BUCKETNAME;

    @Override
    public void afterPropertiesSet() throws Exception {
        ENDPOINT = endpoint;
        KEYID = keyid;
        KEYSECRET = keysecret;
        BUCKETNAME = bucketname;
    }
}
