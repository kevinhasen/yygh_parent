package com.yee.yygh.hosp.api;


import com.yee.yygh.common.result.Result;
import com.yee.yygh.hosp.service.DepartmentService;
import com.yee.yygh.hosp.service.HospitalService;
import com.yee.yygh.hosp.service.ScheduleService;
import com.yee.yygh.model.hosp.Hospital;
import com.yee.yygh.model.hosp.Schedule;
import com.yee.yygh.vo.hosp.DepartmentVo;
import com.yee.yygh.vo.hosp.HospitalQueryVo;
import com.yee.yygh.vo.hosp.ScheduleOrderVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@Api(tags = "医院显示接口")
@RestController
@RequestMapping("/api/hosp/hospital")
public class HospitalApiController {
    @Autowired
    private HospitalService hospitalService;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private ScheduleService scheduleService;

    @ApiOperation(value = "带条件分页查询医院列表")
    @GetMapping("{page}/{limit}")
    public Result index(@PathVariable Integer page,
                        @PathVariable Integer limit,
                        HospitalQueryVo hospitalQueryVo) {
        Page<Hospital> pages =
                hospitalService.selectPage(page,limit,hospitalQueryVo);
        return Result.ok().data("pages",pages);
    }

    @ApiOperation(value = "根据医院名称获取医院列表")
    @GetMapping("findByHosname/{hosname}")
    public Result findByHosname(
            @PathVariable String hosname) {
        List<Hospital> list = hospitalService.findByHosname(hosname);
        return Result.ok().data("list",list);
    }
    @ApiOperation(value = "获取科室列表")
    @GetMapping("department/{hoscode}")
    public Result getDepartmentTree(
            @PathVariable String hoscode) {
        List<DepartmentVo> list = departmentService.findDeptTree(hoscode);
        return Result.ok().data("list",list);
    }

    @ApiOperation(value = "医院预约挂号详情")
    @GetMapping("{hoscode}")
    public Result item(
            @PathVariable String hoscode) {
        Map<String,Object> map = hospitalService.getHospByHoscode(hoscode);
        return Result.ok().data(map);
    }


    @ApiOperation(value = "获取可预约排班数据")
    @GetMapping("auth/getBookingScheduleRule/{page}/{limit}/{hoscode}/{depcode}")
    public Result getBookingSchedule(
            @PathVariable Integer page,
            @PathVariable Integer limit,
            @PathVariable String hoscode,
            @PathVariable String depcode) {
        Map<String,Object> map =
                scheduleService.getBookingScheduleRule(page,limit,hoscode,depcode);
        return Result.ok().data(map);
    }

    @ApiOperation(value = "获取排班数据")
    @GetMapping("auth/findScheduleList/{hoscode}/{depcode}/{workDate}")
    public Result findScheduleList(
            @PathVariable String hoscode,
            @PathVariable String depcode,
            @PathVariable String workDate) {
        List<Schedule> scheduleList =
                scheduleService.getScheduleDetail(hoscode,depcode,workDate);
        return Result.ok().data("scheduleList",scheduleList);
    }

    @ApiOperation(value = "根据排班id获取排班详情")
    @GetMapping("getSchedule/{id}")
    public Result findScheduleById(@PathVariable String id ){
        Schedule schedule = scheduleService.findScheduleById(id);
        return Result.ok().data("schedule",schedule);
    }

    @ApiOperation(value = "根据排班id获取预约下单数据")
    @GetMapping("inner/getScheduleOrderVo/{scheduleId}")
    public ScheduleOrderVo getScheduleOrderVo(
            @PathVariable("scheduleId") String scheduleId) {
        ScheduleOrderVo scheduleOrderVo =
                scheduleService.getScheduleOrderVo(scheduleId);
        return scheduleOrderVo;
    }


}
