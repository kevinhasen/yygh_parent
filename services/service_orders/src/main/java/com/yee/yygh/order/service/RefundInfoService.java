package com.yee.yygh.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yee.yygh.model.order.PaymentInfo;
import com.yee.yygh.model.order.RefundInfo;

/**
 * ClassName: RefundInfoService
 * Description:
 * date: 2022/1/15 15:28
 *
 * @author Yee
 * @since JDK 1.8
 */
public interface RefundInfoService extends IService<RefundInfo> {
    /**
     * 保存退款记录
     * @param paymentInfo
     */
    RefundInfo saveRefundInfo(PaymentInfo paymentInfo);
}
