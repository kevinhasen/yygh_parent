package com.yee.yygh.order.service;

import java.util.Map;

/**
 * ClassName: WeixinService
 * Description:
 * date: 2022/1/14 17:59
 *
 * @author Yee
 * @since JDK 1.8
 */

public interface WeixinService {
    /**
     * 根据订单号下单，生成支付链接
     */
    Map<String, Object> createNative(Long orderId);

    //调用接口查询支付结果
    Map<String, String> queryPayStatus(Long orderId, Integer status);
    /***
     * 退款
     * @param orderId
     * @return
     */
    Boolean refund(Long orderId);
}
