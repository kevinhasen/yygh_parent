package com.yee.yygh.msm.controller;

import com.yee.yygh.common.result.Result;
import com.yee.yygh.msm.service.MsmService;
import com.yee.yygh.msm.util.RandomUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

/**
 * ClassName: MsmController
 * Description:
 * date: 2022/1/4 15:15
 *
 * @author Yee
 * @since JDK 1.8
 */
@Api(tags = "短信发送端口")
@RestController
@RequestMapping("/api/msm")
public class MsmController {

    @Autowired
    private MsmService msmService;
    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    @GetMapping("/send/{phone}")
    @ApiOperation("发送短信")
    public Result send(@PathVariable String phone){
        //先判断有没有验证码
        String redisCode  = redisTemplate.opsForValue().get(phone);
        //1访问redis获取验证码，如果存在直接返回
        if(!StringUtils.isEmpty(redisCode)){
            return Result.ok();
        }
        //随机生成验证码
        String code = RandomUtil.getSixBitRandom();
        boolean isSend = msmService.send(phone,code);
        //如果发送成功则存入redis
        if (isSend){
            //五分钟失效
            redisTemplate.opsForValue().set(phone,code,5, TimeUnit.MINUTES);

            return Result.ok();
        }
        return Result.error();
    }
}
