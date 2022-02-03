package com.yee.yygh.oss;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

/**
 * ClassName: OssApplication
 * Description:
 * date: 2022/1/6 16:41
 *
 * @author Yee
 * @since JDK 1.8
 */
@ComponentScan(basePackages = "com.yee")
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class) //不需要数据源
public class OssApplications {
    public static void main(String[] args) {
        SpringApplication.run(OssApplications.class,args);
    }
}
