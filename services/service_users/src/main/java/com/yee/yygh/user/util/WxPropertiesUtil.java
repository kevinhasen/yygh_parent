package com.yee.yygh.user.util;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * ClassName: WxPropertiesUtil
 * Description:
 * date: 2022/1/5 21:22
 * 读取配置文件微信属性
 * spring初始化bean
 * @author Yee
 * @since JDK 1.8
 */
@Component
public class WxPropertiesUtil implements InitializingBean {

    @Value("${wx.appId}")
    private String appId;
    @Value("${wx.appSecret}")
    private String appSecret;
    @Value("${wx.redirectUrl}")
    private String redirectUrl;

    public static String WX_APP_ID;
    public static String WX_APP_SECRET;
    public static String WX_REDIRECT_URL;

    @Override
    public void afterPropertiesSet() throws Exception {
        WX_APP_ID = appId;
        WX_APP_SECRET = appSecret;
        WX_REDIRECT_URL = redirectUrl;
    }
}
