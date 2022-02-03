package com.yee.yygh.hosp.aop;

import com.yee.yygh.common.handler.YyghException;
import com.yee.yygh.common.result.ResultCodeEnum;
import com.yee.yygh.common.util.MD5;
import com.yee.yygh.hosp.service.HospitalSetService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * ClassName: Aspects
 * Description:
 * date: 2021/12/29 11:46
 * aop签名校验
 * @author Yee
 * @since JDK 1.8
 */
@Component
@Aspect
public class Aspects {
    @Autowired
    private HospitalSetService hospitalSetService;
    @Pointcut("execution(* com.yee.yygh.hosp.api.ApiControllers.*(..))")
    public void point(){
    }

    //方法执行之前
    @Around("point()")
    public Object printBeforeMethod( ProceedingJoinPoint  proceedingJoinPoint) throws Throwable {

        System.out.println("全局aop签名校验");
        Object[] args = proceedingJoinPoint.getArgs();
        HttpServletRequest request = (HttpServletRequest)args[0];
        String hospSign = request.getParameter("sign");
        String hoscode = request.getParameter("hoscode");
        //2.2获取自己签名
        String signKey = hospitalSetService.getSignKey(hoscode);
        //2.3加密签名
        String signKeyMD5 = MD5.encrypt(signKey);
        if(!signKeyMD5.equals(hospSign)){
            throw new YyghException(20001,"签名校验失败");
        }
        return proceedingJoinPoint.proceed();
    }
}
