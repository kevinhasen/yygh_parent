package com.yee.yygh.msm.receiver;

import com.yee.yygh.common.util.MqConst;
import com.yee.yygh.msm.service.MsmService;
import com.yee.yygh.vo.msm.MsmVo;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.amqp.core.Message;
import com.rabbitmq.client.Channel;


/**
 * ClassName: SmsReceiver
 * Description:
 * date: 2022/1/12 23:44
 * 信息监听器
 * @author Yee
 * @since JDK 1.8
 */
@Component
public class SmsReceiver {
    @Autowired
    private MsmService msmService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = MqConst.QUEUE_MSM_ITEM,durable = "true"),
            exchange = @Exchange(value = MqConst.EXCHANGE_DIRECT_MSM),
            key = {MqConst.ROUTING_MSM_ITEM}
    ))
    public void send(MsmVo msmVo, Message message, Channel channel) {
        msmService.send(msmVo);
    }

}
