package com.yee.yygh.task.scheduled;

import com.yee.yygh.common.service.RabbitService;
import com.yee.yygh.common.util.MqConst;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * ClassName: ScheduledTask
 * Description:
 * date: 2022/1/16 10:39
 * 定时任务调度
 * @author Yee
 * @since JDK 1.8
 */
@Component
@EnableScheduling
public class ScheduledTask {

    @Autowired
    private RabbitService rabbitService;
    @Scheduled(cron = "0 0 8 * * ?")
    public void task(){
        rabbitService.sendMessage(MqConst.EXCHANGE_DIRECT_TASK,
                MqConst.ROUTING_TASK_8,
                "");
    }
}
