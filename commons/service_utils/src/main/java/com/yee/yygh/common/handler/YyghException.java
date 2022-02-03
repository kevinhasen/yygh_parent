package com.yee.yygh.common.handler;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ClassName: YyghException
 * Description:
 * date: 2021/12/20 20:05
 *
 * @author Yee
 * @since JDK 1.8
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class YyghException extends RuntimeException{
    @ApiModelProperty("状态码")
    private Integer code;
    @ApiModelProperty("错误信息")
    private String msg;

}
