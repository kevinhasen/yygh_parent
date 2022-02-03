package com.yee.yygh.common.handler;

import com.yee.yygh.common.result.Result;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * ClassName: GlobalExceptionHandler
 * Description:
 * date: 2021/12/20 20:01
 *
 * @author Yee
 * @since JDK 1.8
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Result error(Exception e){
        e.printStackTrace();
        return Result.error();
    }

    @ExceptionHandler(YyghException.class)
    @ResponseBody
    public Result error(YyghException e){
        e.printStackTrace();
        return Result.error().code(e.getCode()).message(e.getMsg());
    }

}
