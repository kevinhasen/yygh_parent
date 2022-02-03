package com.yee.yygh.common.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 全局统一返回结果类
 */
@Data
@ApiModel(value = "全局统一返回结果")
public class Results<T> {

    @ApiModelProperty(value = "返回码")
    private Integer code;

    @ApiModelProperty(value = "返回消息")
    private String message;

    @ApiModelProperty(value = "返回数据")
    private T data;

    public Results(){}

    protected static <T> Results<T> build(T data) {
        Results<T> result = new Results<T>();
        if (data != null)
            result.setData(data);
        return result;
    }

    public static <T> Results<T> build(T body, ResultCodeEnum resultCodeEnum) {
        Results<T> result = build(body);
        result.setCode(resultCodeEnum.getCode());
        result.setMessage(resultCodeEnum.getMessage());
        return result;
    }

    public static <T> Results<T> build(Integer code, String message) {
        Results<T> result = build(null);
        result.setCode(code);
        result.setMessage(message);
        return result;
    }

    public static<T> Results<T> ok(){
        return Results.ok(null);
    }

    /**
     * 操作成功
     * @param data
     * @param <T>
     * @return
     */
    public static<T> Results<T> ok(T data){
        Results<T> result = build(data);
        return build(data, ResultCodeEnum.SUCCESS);
    }

    public static<T> Results<T> fail(){
        return Results.fail(null);
    }

    /**
     * 操作失败
     * @param data
     * @param <T>
     * @return
     */
    public static<T> Results<T> fail(T data){
        Results<T> result = build(data);
        return build(data, ResultCodeEnum.FAIL);
    }

    public Results<T> message(String msg){
        this.setMessage(msg);
        return this;
    }

    public Results<T> code(Integer code){
        this.setCode(code);
        return this;
    }

    public boolean isOk() {
        if(this.getCode().intValue() == ResultCodeEnum.SUCCESS.getCode().intValue()) {
            return true;
        }
        return false;
    }
}
