package com.yee.yygh.user.config;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * ClassName: UserConfig
 * Description:
 * date: 2022/1/4 9:33
 *
 * @author Yee
 * @since JDK 1.8
 */
@ComponentScan(basePackages = "com.yee")//组件扫描路径
@MapperScan("com.yee.yygh.user.mapper")
@Configuration
@EnableDiscoveryClient  //开启nacos发现
@EnableFeignClients(basePackages = "com.yee") //开启feign
public class UserConfig {
    /**
     * 分页插件
     */
    @Bean
    public PaginationInterceptor paginationInterceptor() {
        return new PaginationInterceptor();
    }
}
