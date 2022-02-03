package com.yee.yygh.hosp.receiver;


import com.rabbitmq.client.Channel;
import com.yee.yygh.common.service.RabbitService;
import com.yee.yygh.common.util.MqConst;
import com.yee.yygh.hosp.service.ScheduleService;
import com.yee.yygh.model.hosp.Schedule;
import com.yee.yygh.vo.msm.MsmVo;
import com.yee.yygh.vo.order.OrderMqVo;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class HospitalReceiver {
    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    private RabbitService rabbitService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = MqConst.QUEUE_ORDER,durable = "true"),
            exchange = @Exchange(value = MqConst.EXCHANGE_DIRECT_ORDER),
            key = {MqConst.ROUTING_ORDER}
    ))
    public void receiver(OrderMqVo orderMqVo, Message message,
                         Channel channel) throws IOException {
        //1取出参数
        String hoscode = orderMqVo.getHoscode();
        String hosScheduleId = orderMqVo.getScheduleId();
        System.out.println("hoscode = " + hoscode);
        System.out.println("hosScheduleId = " + hosScheduleId);
        //2根据参数hoscode,hosScheduleId查询排班信息
        Schedule schedule =
                scheduleService.getByHoscodeAndHosScheduleId(hoscode,hosScheduleId);
        //3更新号源信息+取消预约恢复号源信息
        Integer reservedNumber = orderMqVo.getReservedNumber();
        Integer availableNumber = orderMqVo.getAvailableNumber();
        //3.5进行判断
        if(availableNumber!=null){
            //预约下单成功
            schedule.setReservedNumber(reservedNumber);
            schedule.setAvailableNumber(availableNumber);
        }else{
            //取消预约恢复号源信息
            Integer availableNum = schedule.getAvailableNumber();
            schedule.setAvailableNumber(availableNum.intValue()+1);
        }
        scheduleService.update(schedule);

        //4再次发送mq消息（短信消息）
        MsmVo msmVo = orderMqVo.getMsmVo();
        rabbitService.sendMessage(MqConst.EXCHANGE_DIRECT_MSM,
                MqConst.ROUTING_MSM_ITEM,msmVo);

    }

}
