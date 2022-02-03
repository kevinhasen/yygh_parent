package com.yee.yygh.hosp.controller;

import com.baomidou.mybatisplus.extension.api.R;
import com.yee.yygh.common.result.Result;
import com.yee.yygh.hosp.service.ScheduleService;
import com.yee.yygh.model.hosp.Schedule;
import com.yee.yygh.vo.hosp.ScheduleOrderVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * ClassName: ScheduleController
 * Description:
 * date: 2021/12/30 20:24
 *
 * @author Yee
 * @since JDK 1.8
 */
@Api(tags = "排班管理接口")
@RestController
@RequestMapping("/admin/hosp/schedule")
public class ScheduleController{
    @Autowired
    private ScheduleService scheduleService;

    //根据医院编号 和 科室编号 ，查询排班规则数据
    @ApiOperation("查询排班规则数据")
    @GetMapping("/getScheduleRule/{page}/{limit}/{hoscode}/{depcode}")
    public Result getScheduleRule(@PathVariable Long page,
                                  @PathVariable Long limit,
                                  @PathVariable String hoscode,
                                  @PathVariable String depcode){
        Map<String,Object> map = scheduleService
                .getScheduleRule(page,limit,hoscode,depcode);
        return Result.ok().data(map);
    }
    //根据医院编号 、科室编号和工作日期，查询排班详细信息
    @ApiOperation(value = "查询排班详细信息")
    @GetMapping("/getScheduleDetail/{hoscode}/{depcode}/{workDate}")
    public Result getScheduleDetail(@PathVariable String hoscode,
                               @PathVariable String depcode,
                               @PathVariable String workDate){
        List<Schedule> list =
        scheduleService.getScheduleDetail(hoscode,depcode,workDate);
        return Result.ok().data("list",list);
    }


    @ApiOperation(value = "根据排班id获取预约下单数据")
    @GetMapping("inner/getScheduleOrderVo/{scheduleId}")
    public ScheduleOrderVo  getScheduleOrderVo(@PathVariable("scheduleId") String scheduleId){
        ScheduleOrderVo scheduleOrderVo = scheduleService.getScheduleOrderVo(scheduleId);
        return scheduleOrderVo;
    }



}
