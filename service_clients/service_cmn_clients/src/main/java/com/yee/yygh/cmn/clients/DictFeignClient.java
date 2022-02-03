package com.yee.yygh.cmn.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * ClassName: DictFeignClient
 * Description:
 * date: 2021/12/29 19:47
 * feign远程调用
 * 多个接口调用,抽离出来
 * @author Yee
 * @since JDK 1.8
 */
@FeignClient("service-cmn") //服务名,注册中心查找
public interface DictFeignClient {
    //跨模块PathVariable里value夸模块不能省略
    //访问路径要写全
    @GetMapping(value = "/admin/cmn/dict/getName/{parentDictCode}/{value}")
    public String getName(@PathVariable("parentDictCode") String  parentDictCode,
                          @PathVariable("value") String value);
    @GetMapping(value = "/admin/cmn/dict/getName/{value}")
    public String getName(@PathVariable("value") String value);
}
