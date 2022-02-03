package com.yee.yygh.cmn;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

/**
 * ClassName: ServiceCmnApplication
 * Description:
 * date: 2021/12/25 10:14
 *
 * @author Yee
 * @since JDK 1.8
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.yee"})
@EnableDiscoveryClient  //开启nacos发现
public class ServiceCmnApplications {
    public static void main(String[] args) {
        SpringApplication.run(ServiceCmnApplications.class,args);
    }
}
