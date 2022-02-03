package com.yee.yygh.common.config;

import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * ClassName: MQConfig
 * Description:
 * date: 2022/1/12 23:24
 *
 * @author Yee
 * @since JDK 1.8
 */
@Configuration
public class MQConfig {
    @Bean
    public Jackson2JsonMessageConverter messageConverter(){
        return new Jackson2JsonMessageConverter();
    }
}

