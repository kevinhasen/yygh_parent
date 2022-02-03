package com.yee.yygh.order.controller;

import com.yee.yygh.common.result.Result;
import com.yee.yygh.enums.PaymentStatusEnum;
import com.yee.yygh.enums.PaymentTypeEnum;
import com.yee.yygh.order.service.PaymentService;
import com.yee.yygh.order.service.WeixinService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * ClassName: WeixinController
 * Description:
 * date: 2022/1/14 18:00
 *
 * @author Yee
 * @since JDK 1.8
 */
@Api(tags = "微信支付接口")
@RestController
@RequestMapping("/api/order/weixin")
public class WeixinController {
    @Autowired
    private WeixinService weixinService;
    @Autowired
    private PaymentService paymentService;
    /**
     * 下单 生成二维码
     */
    @GetMapping("/createNative/{orderId}")
    public Result createNative(@PathVariable("orderId") Long orderId){
        Map<String,Object> map = weixinService.createNative(orderId);
        return Result.ok().data(map);
    }

    @ApiOperation("查询支付状态")
    @GetMapping("/queryPayStatus/{orderId}")
    public Result queryPayStatus(@PathVariable("orderId") Long orderId){
        //1调用接口查询支付结果
        Map<String,String> resultMap = weixinService.queryPayStatus(
                orderId, PaymentTypeEnum.WEIXIN.getStatus()
        );
        //2判断支付结果失败
        if(resultMap==null){
            return Result.error().message("支付出错");
        }
        //3判断支付结果成功
        if("SUCCESS".equals(resultMap.get("trade_state"))){
            //3.1更改订单状态，处理支付结果
            String out_trade_no = resultMap.get("out_trade_no");
            paymentService.paySuccess(out_trade_no,
                    PaymentTypeEnum.WEIXIN.getStatus(),
                    resultMap
            );
            return Result.ok().message("支付成功");
        }
        //4返回支付中
        return Result.ok().message("支付中");

    }
}
