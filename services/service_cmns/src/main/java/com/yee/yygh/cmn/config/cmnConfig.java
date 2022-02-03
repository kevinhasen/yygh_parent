package com.yee.yygh.cmn.config;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
@MapperScan("com.yee.yygh.cmn.mapper")  //扫描mapper配置
public class cmnConfig {

}
