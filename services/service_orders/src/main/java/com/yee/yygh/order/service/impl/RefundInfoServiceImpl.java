package com.yee.yygh.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yee.yygh.enums.RefundStatusEnum;
import com.yee.yygh.model.order.PaymentInfo;
import com.yee.yygh.model.order.RefundInfo;
import com.yee.yygh.order.mapper.RefundInfoMapper;
import com.yee.yygh.order.service.RefundInfoService;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class RefundInfoServiceImpl
        extends ServiceImpl<RefundInfoMapper, RefundInfo>
        implements RefundInfoService {
    //保存退款记录
    @Override
    public RefundInfo saveRefundInfo(PaymentInfo paymentInfo) {
        //1查询退款记录
        QueryWrapper<RefundInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("order_id",paymentInfo.getOrderId());
        wrapper.eq("payment_type",paymentInfo.getPaymentType());
        RefundInfo refundInfo = baseMapper.selectOne(wrapper);
        if(refundInfo!=null)return refundInfo;
        //2如果查询记录为空，创建新纪录
        refundInfo = new RefundInfo();
        refundInfo.setCreateTime(new Date());
        refundInfo.setOrderId(paymentInfo.getOrderId());
        refundInfo.setPaymentType(paymentInfo.getPaymentType());
        refundInfo.setOutTradeNo(paymentInfo.getOutTradeNo());
        refundInfo.setRefundStatus(RefundStatusEnum.UNREFUND.getStatus());
        refundInfo.setSubject(paymentInfo.getSubject());
        //paymentInfo.setSubject("test");
        refundInfo.setTotalAmount(paymentInfo.getTotalAmount());
        baseMapper.insert(refundInfo);
        return refundInfo;
    }
}
