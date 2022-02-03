package com.yee.yygh.task;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

/**
 * ClassName: ServiceTaskApplication
 * Description:
 * date: 2022/1/16 10:26
 *
 * @author Yee
 * @since JDK 1.8
 */
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class ServiceTaskApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceTaskApplication.class,args);
    }
}
