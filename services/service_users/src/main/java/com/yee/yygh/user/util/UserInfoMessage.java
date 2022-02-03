package com.yee.yygh.user.util;

import com.yee.yygh.model.user.UserInfo;
import org.springframework.util.StringUtils;

import java.util.HashMap;

/**
 * ClassName: UserInfoMessage
 * Description:
 * date: 2022/1/6 15:53
 * 补全用户信息,先不用
 * @author Yee
 * @since JDK 1.8
 */
public class UserInfoMessage {
    public static HashMap<String, String> getMessage( UserInfo userInfo ){
        //补全信息
        HashMap<String, String> map = new HashMap<>();
        String name = userInfo.getName();
        String phone = userInfo.getPhone();
        //如果没有用户名就用昵称或者手机号代替
        if (StringUtils.isEmpty(name)){
            name = userInfo.getNickName();
        }
        if (StringUtils.isEmpty(name)){
            name = phone;
        }
        map.put("name",name);
        map.put("phone",phone);
        return map;
    }
}
