package com.yee.yygh.hosp.config;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * ClassName: HospConfig
 * Description:
 * date: 2021/12/20 18:04
 *
 * @author Yee
 * @since JDK 1.8
 */
@Configuration
@EnableTransactionManagement
@MapperScan("com.yee.yygh.hosp.mapper")
//启用动态代理
@EnableAspectJAutoProxy
@ComponentScan(basePackages = "com.yee")  //组件扫描路径
@EnableDiscoveryClient  //开启nacos发现
@EnableFeignClients(basePackages = "com.yee") //开启feign
public class HospPlusConfig {
    /**
     * 分页插件
     */
    @Bean
    public PaginationInterceptor paginationInterceptor() {
        return new PaginationInterceptor();
    }
}
