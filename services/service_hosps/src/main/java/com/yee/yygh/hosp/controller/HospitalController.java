package com.yee.yygh.hosp.controller;


import com.yee.yygh.common.result.Result;
import com.yee.yygh.hosp.service.HospitalService;
import com.yee.yygh.model.hosp.Hospital;
import com.yee.yygh.vo.hosp.HospitalQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * ClassName: HospitalController
 * Description:
 * date: 2021/12/29 18:34
 * /admin/hosp/hospital
 * admin是给swagger判断
 * hosp是给网关判断
 * @author Yee
 * @since JDK 1.8
 */
@Api(tags = "医院管理接口")
@RestController
@RequestMapping("/admin/hosp/hospital")
//@CrossOrigin  网关统一配置跨域
public class HospitalController {

    @Autowired
    private HospitalService hospitalService;

    @ApiOperation(value = "带条件分页查询医院列表")
    @GetMapping("/{page}/{limit}")
    public Result index(@PathVariable Integer page,
                        @PathVariable Integer limit,
                        HospitalQueryVo hospitalQueryVo){
        Page<Hospital> page1 = hospitalService.selectPage(page,limit,hospitalQueryVo);
    return Result.ok().data("pages",page1);
    }

    @ApiOperation(value = "更新上线状态")
    @GetMapping("/updateStatus/{id}/{status}")
    public Result updateStatus(@PathVariable String id,
                               @PathVariable Integer status){
        hospitalService.lock(id,status);
        return Result.ok();
    }
    @ApiOperation(value = "获取医院详情")
    @GetMapping("show/{id}")
    public Result show(
            @PathVariable String id) {
        Map<String,Object> map = hospitalService.getHospById(id);
        return Result.ok().data(map);
    }

}
