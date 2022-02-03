package com.yee.yygh.user.controller;

import com.baomidou.mybatisplus.extension.api.R;
import com.yee.yygh.common.result.Result;
import com.yee.yygh.common.util.JwtHelper;
import com.yee.yygh.model.user.Patient;
import com.yee.yygh.user.service.PatientService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * ClassName: PatientApiController
 * Description:
 * date: 2022/1/7 21:22
 *
 * @author Yee
 * @since JDK 1.8
 */
@Api(tags = "就诊人管理接口")
@RestController
@RequestMapping("/api/user/patient")
public class PatientApiController {


    @Autowired
    private PatientService patientService;

    //获取就诊人列表
    @ApiOperation(value = "获取就诊人列表")
    @GetMapping("/auth/findAll")
    public Result findAll(HttpServletRequest request){
        Long userId = JwtHelper.getUserId(request.getHeader("token"));
        List<Patient> list =patientService.findAll(userId);
        return Result.ok().data("list",list);
    }

    //添加就诊人
    @ApiOperation(value = "添加就诊人")
    @PostMapping("/auth/save")
    public Result save(@RequestBody Patient patient,
                       HttpServletRequest request){
        Long userId = JwtHelper.getUserId(request.getHeader("token"));
        //将当前登录id和填入的信息填在一起即可
        patient.setUserId(userId);
        patientService.save(patient);
        return Result.ok();
    }

    //根据id获取就诊人信息
    @ApiOperation(value = "获取就诊人")
    @GetMapping("inner/get/{id}")
    public Patient getPatientOrder(@PathVariable("id") Long id){
        Patient patient = patientService.getPatientById(id);
        return patient;
    }

    @ApiOperation(value = "修改就诊人")
    @PostMapping("/auth/update")
    public Result updatePatient(@RequestBody Patient patient){
        patientService.updateById(patient);
        return Result.ok();
    }

    //删除就诊人
    @ApiOperation(value = "删除就诊人")
    @DeleteMapping("/auth/remove/{id}")
    public Result removePatient(@PathVariable Long id){
        patientService.removeById(id);
        return Result.ok();
    }
}
