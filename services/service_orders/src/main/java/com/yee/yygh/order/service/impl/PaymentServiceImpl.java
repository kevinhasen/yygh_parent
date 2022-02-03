package com.yee.yygh.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yee.yygh.common.handler.YyghException;
import com.yee.yygh.enums.OrderStatusEnum;
import com.yee.yygh.enums.PaymentStatusEnum;

import com.yee.yygh.model.order.OrderInfo;
import com.yee.yygh.model.order.PaymentInfo;
import com.yee.yygh.order.mapper.PaymentMapper;
import com.yee.yygh.order.service.OrderService;
import com.yee.yygh.order.service.PaymentService;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

@Service
public class PaymentServiceImpl extends
        ServiceImpl<PaymentMapper, PaymentInfo> implements PaymentService {
    @Autowired
    private OrderService orderService;

    //保存交易记录
    @Override
    public void savePaymentInfo(OrderInfo orderInfo, Integer paymentType) {
        //1查询交易记录
        QueryWrapper<PaymentInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("order_id",orderInfo.getId());
        wrapper.eq("payment_type",paymentType);
        Integer count = baseMapper.selectCount(wrapper);
        if(count>0)return;
        //2查询交易记录为空，再新增交易记录
        PaymentInfo paymentInfo = new PaymentInfo();
        paymentInfo.setCreateTime(new Date());
        paymentInfo.setOrderId(orderInfo.getId());
        paymentInfo.setPaymentType(paymentType);
        paymentInfo.setOutTradeNo(orderInfo.getOutTradeNo());
        paymentInfo.setPaymentStatus(PaymentStatusEnum.UNPAID.getStatus());
        String subject = new DateTime(orderInfo.getReserveDate()).toString("yyyy-MM-dd")+"|"+orderInfo.getHosname()+"|"+orderInfo.getDepname()+"|"+orderInfo.getTitle();
        paymentInfo.setSubject(subject);
        paymentInfo.setTotalAmount(orderInfo.getAmount());
        baseMapper.insert(paymentInfo);
    }

    //更改订单状态，处理支付结果
    @Override
    public void paySuccess(String out_trade_no, Integer paymentType, Map<String, String> resultMap) {
        //1查询交易记录
        PaymentInfo paymentInfo = this.getPaymentInfo(out_trade_no,paymentType);
        if(paymentInfo==null){
            throw  new YyghException(20001,"交易记录失效");
        }
        if(paymentInfo.getPaymentStatus()!=
                PaymentStatusEnum.UNPAID.getStatus()){
            return;
        }
        //2修改支付状态
        PaymentInfo paymentInfoUpd = new PaymentInfo();
        paymentInfoUpd.setPaymentStatus(PaymentStatusEnum.PAID.getStatus());
        paymentInfoUpd.setTradeNo(resultMap.get("transaction_id"));
        paymentInfoUpd.setCallbackTime(new Date());
        paymentInfoUpd.setCallbackContent(resultMap.toString());
        this.updatePaymentInfo(out_trade_no, paymentInfoUpd);
        //3更新订单信息
        OrderInfo orderInfo = orderService.getById(paymentInfo.getOrderId());
        orderInfo.setOrderStatus(OrderStatusEnum.PAID.getStatus());
        orderService.updateById(orderInfo);
        //4更新医院模拟系统订单状态（省略）
    }

    //获取支付记录
    @Override
    public PaymentInfo getPaymentInfo(Long orderId, Integer paymentType) {
        QueryWrapper<PaymentInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("order_id",orderId);
        wrapper.eq("payment_type",paymentType);
        PaymentInfo paymentInfo = baseMapper.selectOne(wrapper);
        return paymentInfo;
    }

    /**
     * 获取支付记录
     */
    private PaymentInfo getPaymentInfo(String outTradeNo, Integer paymentType) {
        QueryWrapper<PaymentInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("out_trade_no", outTradeNo);
        queryWrapper.eq("payment_type", paymentType);
        return baseMapper.selectOne(queryWrapper);
    }

    /**
     * 更改支付记录
     */
    private void updatePaymentInfo(String outTradeNo, PaymentInfo paymentInfoUpd) {
        QueryWrapper<PaymentInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("out_trade_no", outTradeNo);
        baseMapper.update(paymentInfoUpd, queryWrapper);
    }
}
