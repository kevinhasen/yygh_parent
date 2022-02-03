package com.yee.yygh.user.client;


import com.yee.yygh.model.user.Patient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * ClassName: PatientFeignClient
 * Description:
 * date: 2022/1/12 13:11
 *
 * @author Yee
 * @since JDK 1.8
 */
@FeignClient("service-user")
public interface PatientFeignClient {

    //获取就诊人
    @GetMapping("/api/user/patient/inner/get/{id}")
    public Patient getPatientOrder(@PathVariable("id") Long id);

}
