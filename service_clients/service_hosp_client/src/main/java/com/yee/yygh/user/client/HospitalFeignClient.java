package com.yee.yygh.user.client;

import com.yee.yygh.vo.hosp.ScheduleOrderVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * ClassName: HospitalFeignClient
 * Description:
 * date: 2022/1/12 15:03
 *
 * @author Yee
 * @since JDK 1.8
 */
@FeignClient("service-hosp")
public interface HospitalFeignClient {

    @GetMapping("/admin/hosp/schedule/inner/getScheduleOrderVo/{scheduleId}")
    public ScheduleOrderVo getScheduleOrderVo(@PathVariable("scheduleId") String scheduleId);
}
