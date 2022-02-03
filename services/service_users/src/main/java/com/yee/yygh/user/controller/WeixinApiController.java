package com.yee.yygh.user.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yee.yygh.common.handler.YyghException;
import com.yee.yygh.common.result.Result;
import com.yee.yygh.common.util.JwtHelper;
import com.yee.yygh.model.user.UserInfo;
import com.yee.yygh.user.service.UserInfoService;
import com.yee.yygh.user.util.HttpClientUtils;
import com.yee.yygh.user.util.WxPropertiesUtil;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * ClassName: WeixinApiController
 * Description:
 * date: 2022/1/6 9:49
 * 微信控制端
 * @author Yee
 * @since JDK 1.8
 */
@Controller
@RequestMapping("/api/ucenter/wx")
public class WeixinApiController {

    @Autowired
    private UserInfoService userInfoService;

    /**
     * 获取微信登录参数
     */
    @GetMapping("/getLoginParam")
    @ResponseBody
    public Result getLoginParam(HttpSession session) throws UnsupportedEncodingException {
        HashMap<String, Object> map = new HashMap<>();
        //回调地址必须使用urlencode编码
        String redirectUrl = URLEncoder.encode(
                WxPropertiesUtil.WX_REDIRECT_URL,"UTF-8");

        //封装微信需要的数据
        map.put("appid", WxPropertiesUtil.WX_APP_ID);
        map.put("redirectUri", redirectUrl);
        map.put("scope", "snsapi_login");//固定写法,目前只有这个
        map.put("state", System.currentTimeMillis()+"");//需要填写随机码！
        return Result.ok().data(map);
    }
    @ApiOperation("微信回调地址")
    @GetMapping("callback")
    public String callback(String code, String state, HttpSession session){

        //创建请求url
        String tokenUrl = getUserInfo(code);
        try {
            //发送请求拿响应
            String tokenInfo = HttpClientUtils.get(tokenUrl);
        //转化返回结果类型,获得参数
            JSONObject jsonObject = JSONObject.parseObject(tokenInfo);
            String access_token = jsonObject.getString("access_token");
            String openid = jsonObject.getString("openid");
            //根据id查询数据库获得信息
            QueryWrapper<UserInfo> wrapper = new QueryWrapper<>();
            wrapper.eq("openid",openid);
            UserInfo userInfo = userInfoService.getOne(wrapper);
            //如果没有该用户说明第一次注册
            if (userInfo == null){
                //微信获取用户信息
                String baseUserInfo = "https://api.weixin.qq.com/sns/userinfo" +
                        "?access_token=%s" +
                        "&openid=%s";
                String userInfoUrl   = String.format(baseUserInfo,access_token,openid);
                String resultInfo  = HttpClientUtils.get(userInfoUrl);
                JSONObject resultJson = JSONObject.parseObject(resultInfo);
                //解析用户信息
                //用户昵称
                String nickname = resultJson.getString("nickname");
                //用户头像
                String headimgurl = resultJson.getString("headimgurl");
//                System.out.println("头像:"+headimgurl);
                //用户注册
                userInfo = new UserInfo();
                userInfo.setOpenid(openid);
                userInfo.setNickName(nickname);
                userInfo.setStatus(1);
                userInfo.setHeadimgurl(headimgurl);
               userInfoService.save(userInfo);
            }
            //判断是否锁定
            if (userInfo.getStatus() == 0){
            throw new YyghException(20001,"用户已经禁用");
            }

            //5.2 补全信息
            Map<String, String> map = new HashMap<>();
            String name = userInfo.getName();
            if(StringUtils.isEmpty(name)){
                name = userInfo.getNickName();
            }
            if(StringUtils.isEmpty(name)){
                name = userInfo.getPhone();
            }
            map.put("name",name);
            //5.3 判断是否绑定手机号
            String phone = userInfo.getPhone();
            // 如果没绑定手机号，返回openid的值
            //如果绑定了手机号，返回“”
            if(StringUtils.isEmpty(phone)){
                map.put("openid",openid);
            }else{
                map.put("openid","");
            }
            //5.4登录
            String token = JwtHelper.createToken(userInfo.getId(), name);
            map.put("token",token);
            //6 带参数重定向到页面
            String url = "redirect:http://localhost:3000/weixin/callback?token="
                    +map.get("token")+ "&openid="
                    +map.get("openid")
                    +"&name="+URLEncoder.encode(map.get("name"),"utf-8");
            return url;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    private String getUserInfo(String code) {
        //微信临时票据
        StringBuffer buffer = new StringBuffer();
        buffer.append("https://api.weixin.qq.com/sns/oauth2/access_token")
                .append("?appid=%s")
                .append("&secret=%s")
                .append("&code=%s")
                .append("&grant_type=authorization_code");//固定写法
        //填充占位符
        String tokenUrl = String.format(buffer.toString(),
                WxPropertiesUtil.WX_APP_ID,
                WxPropertiesUtil.WX_APP_SECRET,code);
        return tokenUrl;
    }


}
