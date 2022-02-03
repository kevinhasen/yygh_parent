package com.yee.yygh.sta.config;

import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * ClassName: StaConfig
 * Description:
 * date: 2022/1/16 18:55
 *
 * @author Yee
 * @since JDK 1.8
 */
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.yee")
@ComponentScan(basePackages = "com.yee")
@Configuration
public class StaConfig {
}
