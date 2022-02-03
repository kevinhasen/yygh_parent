package com.yee.yygh.hosp.controller;

import com.yee.yygh.common.result.Result;
import com.yee.yygh.hosp.service.DepartmentService;
import com.yee.yygh.vo.hosp.DepartmentVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * ClassName: DepartmentController
 * Description:
 * date: 2021/12/30 15:59
 *
 * @author Yee
 * @since JDK 1.8
 */
@Api(tags = "科室列表")
@RestController
@RequestMapping("/admin/hosp/department")
public class DepartmentController {
    @Autowired
    private DepartmentService departmentService;

    //根据医院编号，查询医院所有科室列表
    @ApiOperation("查询医院所有科室列表")
    @GetMapping("/getDeptList/{hoscode}")
    public Result getDepList(@PathVariable String hoscode){
        List<DepartmentVo> list = departmentService.findDeptTree(hoscode);
        return Result.ok().data("list",list);
    }
}
