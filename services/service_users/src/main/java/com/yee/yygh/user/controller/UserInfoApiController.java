package com.yee.yygh.user.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yee.yygh.common.handler.YyghException;
import com.yee.yygh.common.result.Result;
import com.yee.yygh.common.util.JwtHelper;
import com.yee.yygh.model.user.UserInfo;
import com.yee.yygh.user.service.UserInfoService;
import com.yee.yygh.user.util.IpUtils;
import com.yee.yygh.vo.user.LoginVo;
import com.yee.yygh.vo.user.UserAuthVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * ClassName: UserInfoApiController
 * Description:
 * date: 2022/1/4 10:05
 *
 * @author Yee
 * @since JDK 1.8
 */
@RestController
@RequestMapping("/api/user")
public class UserInfoApiController {

    @Autowired
    private UserInfoService userInfoService;

    @ApiOperation(value = "会员登录")
    @PostMapping("login")
    public Result login(@RequestBody LoginVo loginVo, HttpServletRequest request){


        //获得ip地址
        String addr = IpUtils.getIpAddr(request);
        //封装ip地址
        loginVo.setIp(addr);
        Map<String,Object> map  = userInfoService.login(loginVo);
        return Result.ok().data(map);
    }

    //获得微信头像
    @ApiOperation(value = "获得微信头像")
    @GetMapping("/getHeadimgurl/{name}")
    public Result getHeadimgurl(@PathVariable("name") String name){
        QueryWrapper<UserInfo> wrapper = new QueryWrapper();
        wrapper.like("phone",name);
        UserInfo userInfo  = userInfoService.getOne(wrapper);
        String headimgurl = userInfo.getHeadimgurl();
        return Result.ok().data("headimgurl",headimgurl);
    }

    @ApiOperation("用户认证接口")
    @PostMapping("/auth/userAuth")
    public Result userAuth(@RequestBody UserAuthVo userAuthVo,
                           HttpServletRequest request){
        Long userId = JwtHelper.getUserId(request.getHeader("token"));
        userInfoService.userAuth(userId,userAuthVo);
        return Result.ok();
    }
    //查询认证信息
    @ApiOperation("查询认证信息")
    @GetMapping("/auth/getUserInfo")
    public Result getUserInfo( HttpServletRequest request){
        Long userId = JwtHelper.getUserId(request.getHeader("token"));
        UserInfo userInfo = userInfoService.getUserInfo(userId);
        return Result.ok().data("userInfo",userInfo);
    }
}
