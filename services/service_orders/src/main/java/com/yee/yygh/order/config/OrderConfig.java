package com.yee.yygh.order.config;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * ClassName: OrderConfig
 * Description:
 * date: 2022/1/11 23:53
 *
 * @author Yee
 * @since JDK 1.8
 */
@Configuration
@ComponentScan(basePackages = "com.yee")
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.yee")
@MapperScan("com.yee.yygh.order.mapper")
public class OrderConfig {
    /**
     * 分页插件
     */
    @Bean
    public PaginationInterceptor paginationInterceptor() {
        return new PaginationInterceptor();
    }

}
