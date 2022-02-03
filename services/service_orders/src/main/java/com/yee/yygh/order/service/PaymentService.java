package com.yee.yygh.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yee.yygh.model.order.OrderInfo;
import com.yee.yygh.model.order.PaymentInfo;

import java.util.Map;

/**
 * ClassName: PaymentService
 * Description:
 * date: 2022/1/13 23:13
 *
 * @author Yee
 * @since JDK 1.8
 */
public interface PaymentService  extends IService<PaymentInfo> {
    /**
     * 保存交易记录
     * @param orderInfo
     * @param paymentType 支付类型（1：微信 2：支付宝）
     */
    void savePaymentInfo(OrderInfo orderInfo, Integer paymentType);
    /**
     * 获取支付记录
     * @param orderId
     * @param paymentType
     * @return
     */
    PaymentInfo getPaymentInfo(Long orderId, Integer paymentType);

    //更改订单状态，处理支付结果
    void paySuccess(String outTradeNo, Integer paymentType, Map<String, String> resultMap);
}
