package com.yee.yygh.order.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yee.yygh.common.handler.YyghException;
import com.yee.yygh.common.service.RabbitService;
import com.yee.yygh.common.util.MqConst;
import com.yee.yygh.enums.OrderStatusEnum;
import com.yee.yygh.model.order.OrderInfo;
import com.yee.yygh.model.user.Patient;
import com.yee.yygh.order.mapper.OrderInfoMapper;
import com.yee.yygh.order.service.OrderService;
import com.yee.yygh.order.service.WeixinService;
import com.yee.yygh.order.util.HttpRequestHelper;
import com.yee.yygh.user.client.HospitalFeignClient;
import com.yee.yygh.user.client.PatientFeignClient;
import com.yee.yygh.vo.hosp.ScheduleOrderVo;
import com.yee.yygh.vo.msm.MsmVo;

import com.yee.yygh.vo.order.OrderCountQueryVo;
import com.yee.yygh.vo.order.OrderCountVo;
import com.yee.yygh.vo.order.OrderMqVo;
import com.yee.yygh.vo.order.OrderQueryVo;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;


@Service
public class OrderServiceImpl extends ServiceImpl<OrderInfoMapper, OrderInfo>
        implements OrderService {
    @Autowired
    private PatientFeignClient patientFeignClient;
    @Autowired
    private HospitalFeignClient hospitalFeignClient;
    @Autowired
    private RabbitService rabbitService;
    @Autowired
    private WeixinService weixinService;

    //创建订单
    @Override
    public Long submitOrder(String scheduleId, Long patientId) {
        //1根据patientId跨模块(user)调用,获取就诊人信息
        Patient patient = patientFeignClient.getPatientOrder(patientId);
        if(patient==null){
            throw new YyghException(20001,"获取就诊人信息失败");
        }
        //2根据scheduleId跨模块(hosp)调用,获取排班相关信息
        ScheduleOrderVo scheduleOrderVo =
                hospitalFeignClient.getScheduleOrderVo(scheduleId);
        if(scheduleOrderVo==null){
            throw new YyghException(20001,"获取排班相关信息失败");
        }
        //当前时间不可以预约
        if(new DateTime(scheduleOrderVo.getStartTime()).isAfterNow()
                || new DateTime(scheduleOrderVo.getEndTime()).isBeforeNow()) {
            throw new YyghException(20001,"当前时间不可以预约");
        }
        if(scheduleOrderVo.getAvailableNumber() <= 0) {
            throw new YyghException(20001,"号已挂满");
        }

        //3整合数据，生成订单，入库
        OrderInfo orderInfo = new OrderInfo();
        BeanUtils.copyProperties(scheduleOrderVo,orderInfo);
        String outTradeNo = System.currentTimeMillis() + ""+ new Random().nextInt(100);
        orderInfo.setOutTradeNo(outTradeNo);
        orderInfo.setUserId(patient.getUserId());
        orderInfo.setPatientId(patientId);
        orderInfo.setPatientName(patient.getName());
        orderInfo.setPatientPhone(patient.getPhone());
        orderInfo.setOrderStatus(OrderStatusEnum.UNPAID.getStatus());
        this.save(orderInfo);
        //4调用医院模拟系统，成功后获取挂号信息，更新订单
        //4.1收集参数
        Map<String,Object> paramMap = new HashMap<>();
        paramMap.put("hoscode",orderInfo.getHoscode());
        paramMap.put("depcode",orderInfo.getDepcode());
        paramMap.put("hosScheduleId",orderInfo.getHosScheduleId());
        paramMap.put("reserveDate",new DateTime(orderInfo.getReserveDate()).toString("yyyy-MM-dd"));
        paramMap.put("reserveTime", orderInfo.getReserveTime());
        paramMap.put("amount",orderInfo.getAmount());
        paramMap.put("name", patient.getName());
        paramMap.put("certificatesType",patient.getCertificatesType());
        paramMap.put("certificatesNo", patient.getCertificatesNo());
        paramMap.put("sex",patient.getSex());
        paramMap.put("birthdate", patient.getBirthdate());
        paramMap.put("phone",patient.getPhone());
        paramMap.put("isMarry", patient.getIsMarry());
        paramMap.put("provinceCode",patient.getProvinceCode());
        paramMap.put("cityCode", patient.getCityCode());
        paramMap.put("districtCode",patient.getDistrictCode());
        paramMap.put("address",patient.getAddress());
        //联系人
        paramMap.put("contactsName",patient.getContactsName());
        paramMap.put("contactsCertificatesType", patient.getContactsCertificatesType());
        paramMap.put("contactsCertificatesNo",patient.getContactsCertificatesNo());
        paramMap.put("contactsPhone",patient.getContactsPhone());
        paramMap.put("timestamp", HttpRequestHelper.getTimestamp());
        //String sign = HttpRequestHelper.getSign(paramMap, signInfoVo.getSignKey());
        paramMap.put("sign", "");
        //4.2调用接口获取结果
        JSONObject result = HttpRequestHelper.sendRequest(paramMap,
                "http://localhost:9998/order/submitOrder");

        if(result.getInteger("code")==200){
            //4.3成功后，获取返回数据
            JSONObject jsonObject = result.getJSONObject("data");
            //预约记录唯一标识（医院预约记录主键）
            String hosRecordId = jsonObject.getString("hosRecordId");
            //预约序号
            Integer number = jsonObject.getInteger("number");
            //取号时间
            String fetchTime = jsonObject.getString("fetchTime");
            //取号地址
            String fetchAddress = jsonObject.getString("fetchAddress");
            //4.4更新订单信息
            orderInfo.setHosRecordId(hosRecordId);
            orderInfo.setNumber(number);
            orderInfo.setFetchTime(fetchTime);
            orderInfo.setFetchAddress(fetchAddress);
            baseMapper.updateById(orderInfo);
            //排班可预约数
            Integer reservedNumber = jsonObject.getInteger("reservedNumber");
            //排班剩余预约数
            Integer availableNumber = jsonObject.getInteger("availableNumber");
            //5挂号成功后，更新号源信息，发送通知短信
            //5.1封装订单消息
            OrderMqVo orderMqVo = new OrderMqVo();
            orderMqVo.setHoscode(orderInfo.getHoscode());
            orderMqVo.setScheduleId(orderInfo.getHosScheduleId());
            orderMqVo.setReservedNumber(reservedNumber);
            orderMqVo.setAvailableNumber(availableNumber);
            //5.2封装短信
            MsmVo msmVo = new MsmVo();
            msmVo.setPhone(orderInfo.getPatientPhone());
            String reserveDate =
                    new DateTime(orderInfo.getReserveDate()).toString("yyyy-MM-dd")
                            + (orderInfo.getReserveTime()==0 ? "上午": "下午");
            Map<String,Object> param = new HashMap<String,Object>(){{
                put("title", orderInfo.getHosname()+"|"+orderInfo.getDepname()+"|"+orderInfo.getTitle());
                put("amount", orderInfo.getAmount());
                put("reserveDate", reserveDate);
                put("name", orderInfo.getPatientName());
                put("quitTime", new DateTime(orderInfo.getQuitTime()).toString("yyyy-MM-dd HH:mm"));
            }};
            msmVo.setParam(param);
            orderMqVo.setMsmVo(msmVo);
            //5.3发送订单消息
            rabbitService.sendMessage(MqConst.EXCHANGE_DIRECT_ORDER,
                    MqConst.ROUTING_ORDER, orderMqVo);

        }
        return orderInfo.getId();
    }

    //带分页带条件订单列表查询
    @Override
    public IPage<OrderInfo> selectPage(IPage<OrderInfo> pageParam,
                                       OrderQueryVo orderQueryVo) {
        //1取出参数
        Long userId = orderQueryVo.getUserId();
        String name = orderQueryVo.getKeyword(); //医院名称
        Long patientId = orderQueryVo.getPatientId(); //就诊人名称
        String orderStatus = orderQueryVo.getOrderStatus(); //订单状态
        String reserveDate = orderQueryVo.getReserveDate();//安排时间
        String createTimeBegin = orderQueryVo.getCreateTimeBegin();
        String createTimeEnd = orderQueryVo.getCreateTimeEnd();
        //2验空，拼写查询条件
        QueryWrapper<OrderInfo> wrapper = new QueryWrapper<>();
        if(!StringUtils.isEmpty(userId)){
            wrapper.eq("user_id",userId);
        }
        if(!StringUtils.isEmpty(name)) {
            wrapper.like("hosname",name);
        }
        if(!StringUtils.isEmpty(patientId)) {
            wrapper.eq("patient_id",patientId);
        }
        if(!StringUtils.isEmpty(orderStatus)) {
            wrapper.eq("order_status",orderStatus);
        }
        if(!StringUtils.isEmpty(reserveDate)) {
            wrapper.ge("reserve_date",reserveDate);
        }
        if(!StringUtils.isEmpty(createTimeBegin)) {
            wrapper.ge("create_time",createTimeBegin);
        }
        if(!StringUtils.isEmpty(createTimeEnd)) {
            wrapper.le("create_time",createTimeEnd);
        }
        //3分页查询返回结果
        //3.1分页查询
        IPage<OrderInfo> pageModel = baseMapper.selectPage(pageParam, wrapper);
        //3.2翻译字段
        pageModel.getRecords().stream().forEach(item->{
            this.packOrderInfo(item);
        });
        return pageModel;
    }

    //获取订单详情
    @Override
    public OrderInfo getOrderById(Long orderId) {
        OrderInfo orderInfo = this.packOrderInfo(baseMapper.selectById(orderId)) ;
        return orderInfo;
    }

    //取消预约
    @Override
    public Boolean cancelOrder(Long orderId) {
        //1根据orderId查询订单
        OrderInfo orderInfo = baseMapper.selectById(orderId);
        if(orderInfo==null){
            throw  new YyghException(20001,"订单信息有误");
        }
        //2判断退号时间是否已过
        DateTime quitTime = new DateTime(orderInfo.getQuitTime());
        if(quitTime.isBeforeNow()){
            throw  new YyghException(20001,"已过退号截止时间");
        }
        //3调用医院模拟系统进行退号操作
        Map<String,Object> reqMap = new HashMap<>();
        reqMap.put("hoscode",orderInfo.getHoscode());
        reqMap.put("hosRecordId",orderInfo.getHosRecordId());
        reqMap.put("timestamp", HttpRequestHelper.getTimestamp());
        reqMap.put("sign", "");
        JSONObject result = HttpRequestHelper.sendRequest(reqMap,
                "http://localhost:9998/order/updateCancelStatus");
        if(result.getInteger("code")!=200){
            throw  new YyghException(20001,result.getString("message"));
        }else{
            //4退号成功后判断是否支付
            if(orderInfo.getOrderStatus().intValue()==
                    OrderStatusEnum.PAID.getStatus().intValue()){
                //5如果已支付，调用接口实现微信退款
                Boolean isRefund = weixinService.refund(orderInfo.getId());
                if(!isRefund){
                    throw  new YyghException(20001,"微信退款失败");
                }
            }
            //6更改订单状态
            orderInfo.setOrderStatus(OrderStatusEnum.CANCLE.getStatus());
            baseMapper.updateById(orderInfo);
            //7更改号源信息，发送通知短信
            //7.1封装订单消息
            OrderMqVo orderMqVo = new OrderMqVo();
            orderMqVo.setHoscode(orderInfo.getHoscode());
            orderMqVo.setScheduleId(orderInfo.getHosScheduleId());
            //7.2封装短信消息
            MsmVo msmVo = new MsmVo();
            msmVo.setPhone(orderInfo.getPatientPhone());
            orderMqVo.setMsmVo(msmVo);
            rabbitService.sendMessage(MqConst.EXCHANGE_DIRECT_ORDER,
                    MqConst.ROUTING_ORDER, orderMqVo);

        }
        return true;
    }



    //就诊提醒
    @Override
    public void patientTips() {

        //1查询数据库
        QueryWrapper<OrderInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("reserve_date",new DateTime().toString("yyyy-MM-dd"));
        List<OrderInfo> orderInfoList = baseMapper.selectList(wrapper);
        //2遍历数据发送短信
        for (OrderInfo orderInfo : orderInfoList) {
            //短信提示
            MsmVo msmVo = new MsmVo();
            msmVo.setPhone(orderInfo.getPatientPhone());
            String reserveDate = new DateTime(orderInfo.getReserveDate()).toString("yyyy-MM-dd") + (orderInfo.getReserveTime()==0 ? "上午": "下午");
            Map<String,Object> param = new HashMap<String,Object>(){{
                put("title", orderInfo.getHosname()+"|"+orderInfo.getDepname()+"|"+orderInfo.getTitle());
                put("reserveDate", reserveDate);
                put("name", orderInfo.getPatientName());
            }};
            msmVo.setParam(param);
            rabbitService.sendMessage(MqConst.EXCHANGE_DIRECT_MSM, MqConst.ROUTING_MSM_ITEM, msmVo);
        }
    }

//    获取订单统计数据
    @Override
    public Map<String, Object> getCountMap(OrderCountQueryVo orderCountQueryVo) {
        //1查询统计数据
        List<OrderCountVo> orderCountVoList = baseMapper.selectOrderCount(orderCountQueryVo);
        //2封装数据（x轴、y轴）
        Map<String, Object> map = new HashMap<>();
        //x轴
        List<String> dateList = orderCountVoList.stream().map(OrderCountVo::getReserveDate)
                .collect(Collectors.toList());
        //y轴
        List<Integer> countList = orderCountVoList.stream().map(OrderCountVo::getCount)
                .collect(Collectors.toList());
        map.put("dateList", dateList);
        map.put("countList", countList);
        return map;
    }

    //翻译字段
    private OrderInfo packOrderInfo(OrderInfo orderInfo) {
        orderInfo.getParam().put("orderStatusString", OrderStatusEnum.getStatusNameByStatus(orderInfo.getOrderStatus()));
        return orderInfo;
    }
}

