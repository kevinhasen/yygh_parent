package com.yee.yygh.sta;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * ClassName: ServiceStatisticsApplication
 * Description:
 * date: 2022/1/16 18:54
 *
 * @author Yee
 * @since JDK 1.8
 */
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class ServiceStatisticsApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceStatisticsApplication.class,args);
    }
}
