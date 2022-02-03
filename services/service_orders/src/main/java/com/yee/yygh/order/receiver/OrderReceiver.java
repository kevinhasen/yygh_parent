package com.yee.yygh.order.receiver;

import com.rabbitmq.client.Channel;
import com.yee.yygh.common.util.MqConst;
import com.yee.yygh.order.service.OrderService;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * ClassName: OrderReceiver
 * Description:
 * date: 2022/1/16 10:54
 *
 * @author Yee
 * @since JDK 1.8
 */
@Component
public class OrderReceiver {
    @Autowired
    private OrderService orderService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = MqConst.QUEUE_TASK_8,durable = "true"),
            exchange = @Exchange(MqConst.EXCHANGE_DIRECT_TASK),
            key = MqConst.ROUTING_TASK_8
    ))
    public void patientTips(Message message, Channel channel){
        orderService.patientTips();
    }
}
