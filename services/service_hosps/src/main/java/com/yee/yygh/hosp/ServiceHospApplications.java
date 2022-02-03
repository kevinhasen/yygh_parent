package com.yee.yygh.hosp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * ClassName: ServiceHospApplication
 * Description:
 * date: 2021/12/20 17:55
 *
 * @author Yee
 * @since JDK 1.8
 */
@SpringBootApplication
public class ServiceHospApplications {
    public static void main(String[] args) {
        SpringApplication.run(ServiceHospApplications.class,args);
    }
}
