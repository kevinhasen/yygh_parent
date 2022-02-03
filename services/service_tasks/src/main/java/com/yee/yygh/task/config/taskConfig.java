package com.yee.yygh.task.config;

import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * ClassName: taskConfig
 * Description:
 * date: 2022/1/16 10:28
 *
 * @author Yee
 * @since JDK 1.8
 */
@Configuration
@ComponentScan(basePackages = "com.yee")
@EnableDiscoveryClient
public class taskConfig {

}
