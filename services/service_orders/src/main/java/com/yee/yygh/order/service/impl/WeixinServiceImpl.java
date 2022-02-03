package com.yee.yygh.order.service.impl;

import com.alibaba.fastjson.JSONObject;

import com.github.wxpay.sdk.WXPayConstants;
import com.github.wxpay.sdk.WXPayUtil;
import com.yee.yygh.common.handler.YyghException;
import com.yee.yygh.enums.PaymentTypeEnum;
import com.yee.yygh.enums.RefundStatusEnum;
import com.yee.yygh.model.order.OrderInfo;
import com.yee.yygh.model.order.PaymentInfo;
import com.yee.yygh.model.order.RefundInfo;

import com.yee.yygh.order.service.OrderService;
import com.yee.yygh.order.service.PaymentService;
import com.yee.yygh.order.service.RefundInfoService;
import com.yee.yygh.order.service.WeixinService;
import com.yee.yygh.order.util.ConstantPropertiesUtils;
import com.yee.yygh.order.util.HttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class WeixinServiceImpl implements WeixinService {
    @Autowired
    private OrderService orderService;
    @Autowired
    private PaymentService paymentService;
    @Autowired
    private RefundInfoService refundInfoService;
    //生成二维码
    @Override
    public Map<String, Object> createNative(Long orderId) {
        try {
            //1、根据orderId获取订单信息
            OrderInfo orderInfo = orderService.getById(orderId);
            if(orderInfo==null){
                throw  new YyghException(20001,"订单信息有误");
            }
            //2、添加交易记录
            paymentService.savePaymentInfo(
                    orderInfo, PaymentTypeEnum.WEIXIN.getStatus());
            //3、用map封装参数
            Map paramMap = new HashMap();
            paramMap.put("appid", ConstantPropertiesUtils.APPID);
            paramMap.put("mch_id", ConstantPropertiesUtils.PARTNER);
            paramMap.put("nonce_str", WXPayUtil.generateNonceStr());
            String body = orderInfo.getReserveDate() + "就诊"+ orderInfo.getDepname();
            paramMap.put("body", body);
            paramMap.put("out_trade_no", orderInfo.getOutTradeNo());
            //paramMap.put("total_fee", order.getAmount().multiply(new BigDecimal("100")).longValue()+"");
            paramMap.put("total_fee", "1");//为了测试
            paramMap.put("spbill_create_ip", "127.0.0.1");
            paramMap.put("notify_url", "http://guli.shop/api/order/weixinPay/weixinNotify");
            paramMap.put("trade_type", "NATIVE");
            //4、创建客户端对象（请求url）
            HttpClient client = new HttpClient(
                    "https://api.mch.weixin.qq.com/pay/unifiedorder");
            //5、给客户端设置参数（map=> xml）
            client.setXmlParam(WXPayUtil.generateSignedXml(paramMap,
                    ConstantPropertiesUtils.PARTNERKEY ));
            client.setHttps(true);
            //6、发送请求，获取响应（xml=>map）
            client.post();
            String xml = client.getContent();
            System.out.println("xml = " + xml);
            Map<String, String> resultMap = WXPayUtil.xmlToMap(xml);
            //7、封装数据返回
            Map<String, Object> map = new HashMap<>();
            map.put("orderId", orderId);
            map.put("totalFee", orderInfo.getAmount());
            map.put("resultCode", resultMap.get("result_code"));
            map.put("codeUrl", resultMap.get("code_url"));
            return map;
        } catch (Exception e) {
            e.printStackTrace();
            throw new YyghException(20001,"获取二维码失败");
        }
    }

    //调用接口查询支付结果
    @Override
    public Map<String, String> queryPayStatus(Long orderId, Integer status) {
        try {
            //1根据orderId查询订单信息
            OrderInfo orderInfo = orderService.getById(orderId);
            if(orderInfo==null){
                throw  new YyghException(20001,"订单信息有误");
            }
            //2用map封装参数
            Map paramMap = new HashMap();
            paramMap.put("appid", ConstantPropertiesUtils.APPID);
            paramMap.put("mch_id", ConstantPropertiesUtils.PARTNER);
            paramMap.put("out_trade_no", orderInfo.getOutTradeNo());
            paramMap.put("nonce_str", WXPayUtil.generateNonceStr());

            //3创建客户端对象,存入参数，发送请求
            HttpClient client = new HttpClient(
                    "https://api.mch.weixin.qq.com/pay/orderquery");
            client.setXmlParam(WXPayUtil.generateSignedXml(paramMap,
                    ConstantPropertiesUtils.PARTNERKEY ));
            client.setHttps(true);
            client.post();
            //4获取参数，返回
            String xml = client.getContent();
            System.out.println("queryPayStatusxml = " + xml);
            Map<String, String> resultMap = WXPayUtil.xmlToMap(xml);
            return resultMap;
        } catch (Exception e) {
            e.printStackTrace();
            throw  new YyghException(20001,"查询支付结果失败");
        }
    }

    //退款
    @Override
    public Boolean refund(Long orderId) {
        try {
            //1根据orderId查询交易信息
            PaymentInfo paymentInfo = paymentService.getPaymentInfo(orderId, PaymentTypeEnum.WEIXIN.getStatus());
            if(paymentInfo==null){
                throw  new YyghException(20001,"交易信息有误");
            }
            //2生成退款记录，并获取
            RefundInfo refundInfo = refundInfoService.saveRefundInfo(paymentInfo);
            if(refundInfo.getRefundStatus().intValue()== RefundStatusEnum.REFUND.getStatus().intValue()){
                return true;
            }
            //3用map封装参数
            Map<String,String> paramMap = new HashMap<>();
            paramMap.put("appid",ConstantPropertiesUtils.APPID);       //公众账号ID
            paramMap.put("mch_id",ConstantPropertiesUtils.PARTNER);   //商户编号
            paramMap.put("nonce_str",WXPayUtil.generateNonceStr());
            paramMap.put("transaction_id",paymentInfo.getTradeNo()); //微信订单号
            paramMap.put("out_trade_no",paymentInfo.getOutTradeNo()); //商户订单编号
            paramMap.put("out_refund_no","tk"+paymentInfo.getOutTradeNo()); //商户退款单号
            //       paramMap.put("total_fee",paymentInfoQuery.getTotalAmount().multiply(new BigDecimal("100")).longValue()+"");
            //       paramMap.put("refund_fee",paymentInfoQuery.getTotalAmount().multiply(new BigDecimal("100")).longValue()+"");
            paramMap.put("total_fee","1");
            paramMap.put("refund_fee","1");
            String paramXml = WXPayUtil.generateSignedXml(paramMap,ConstantPropertiesUtils.PARTNERKEY);
            //4创建客户端对象（请求url）
            HttpClient client = new HttpClient("https://api.mch.weixin.qq.com/secapi/pay/refund");
            //5给客户端设置参数
            client.setXmlParam(paramXml);
            client.setHttps(true);
            //设置证书相关参数
            client.setCert(true);
            client.setCertPassword(ConstantPropertiesUtils.PARTNER);
            //6发送请求获取响应
            client.post();
            String xml = client.getContent();
            System.out.println("refundInfoxml = " + xml);
            //7退款成功后更新退款记录
            Map<String, String> resultMap = WXPayUtil.xmlToMap(xml);
            if (null != resultMap && WXPayConstants.SUCCESS.equalsIgnoreCase(resultMap.get("result_code"))) {
                refundInfo.setCallbackTime(new Date());
                refundInfo.setTradeNo(resultMap.get("refund_id"));
                refundInfo.setRefundStatus(RefundStatusEnum.REFUND.getStatus());
                refundInfo.setCallbackContent(JSONObject.toJSONString(resultMap));
                refundInfoService.updateById(refundInfo);
                return true;

            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
