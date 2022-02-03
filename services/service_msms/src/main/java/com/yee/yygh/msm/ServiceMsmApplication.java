package com.yee.yygh.msm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

/**
 * ClassName: ServiceMsmApplication
 * Description:
 * date: 2022/1/4 15:06
 *
 * @author Yee
 * @since JDK 1.8
 */
//取消数据源自动配置,否则父工程会自动配置数据库
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@ComponentScan({"com.yee"})
public class ServiceMsmApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceMsmApplication.class,args);
    }
}
