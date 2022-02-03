package com.yee.yygh.hosp.api;

import com.yee.yygh.common.handler.YyghException;
import com.yee.yygh.common.result.Results;
import com.yee.yygh.common.util.HttpRequestHelper;
import com.yee.yygh.common.util.MD5;
import com.yee.yygh.hosp.service.DepartmentService;
import com.yee.yygh.hosp.service.HospitalService;
import com.yee.yygh.hosp.service.HospitalSetService;
import com.yee.yygh.hosp.service.ScheduleService;
import com.yee.yygh.model.hosp.Department;
import com.yee.yygh.model.hosp.Hospital;
import com.yee.yygh.vo.hosp.DepartmentQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Api(tags = "医院管理API接口")
@RestController
@RequestMapping("/api/hosp")
public class ApiControllers {
    @Autowired
    private HospitalService hospitalService;
    @Autowired
    private HospitalSetService hospitalSetService;

    @Autowired
    private DepartmentService departmentService;
    @Autowired
    private ScheduleService scheduleService;

    @ApiOperation(value = "上传医院")
    @PostMapping("saveHospital")
    public Results saveHospital(HttpServletRequest request) {
        //1获取参数进行封装
        Map<String, String[]> parameterMap = request.getParameterMap();
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(parameterMap);
        //2  签名校验
        //2.1获取医院签名、医院编码
//        String hospSign = (String)paramMap.get("sign");
//        String hoscode = (String)paramMap.get("hoscode");
//        //2.2获取自己签名
//        String signKey = hospitalSetService.getSignKey(hoscode);
//        //2.3加密签名
//        String signKeyMD5 = MD5.encrypt(signKey);
//        if(!signKeyMD5.equals(hospSign)){
//            throw new YyghException(20001,"签名校验失败");
//        }

        //3 参数存入数据库
        //传输过程中“+”转换为了“ ”，因此我们要转换回来
//        String logoData = (String)paramMap.get("logoData");
//        logoData = logoData.replaceAll(" ","+");
//        paramMap.put("logoData",logoData);
        //后期使用oss替换base64

        hospitalService.saveHospital(paramMap);
        return Results.ok();
    }

    @ApiOperation(value = "获取医院信息")
    @PostMapping("hospital/show")
    public Results hospital(HttpServletRequest request) {
        //1获取参数进行封装
        Map<String, String[]> parameterMap = request.getParameterMap();
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(parameterMap);
        //2取出参数
//        String sign = (String)paramMap.get("sign");
        String hoscode = (String)paramMap.get("hoscode");
        //3签名校验（省略）
        //4 查询医院信息
        Hospital hospital =  hospitalService.selectHosp(hoscode);
        return Results.ok(hospital);
    }

    @ApiOperation(value = "上传科室")
    @PostMapping("saveDepartment")
    public Results saveDepartment(HttpServletRequest request) {
        //1获取参数进行封装
        Map<String, String[]> parameterMap = request.getParameterMap();
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(parameterMap);
        //2关键参数校验（省略）
        //3签名校验（省略）
        departmentService.save(paramMap);
        return Results.ok();
    }

    @ApiOperation(value = "获取分页列表")
    @PostMapping("department/list")
    public Results department(HttpServletRequest request) {
        //1获取参数进行封装
        Map<String, String[]> parameterMap = request.getParameterMap();
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(parameterMap);
        //2获取参数
        String hoscode = (String) paramMap.get("hoscode");
        int page = StringUtils.isEmpty(paramMap.get("page"))
                ? 1 : Integer.parseInt((String) paramMap.get("page"));
        int limit = StringUtils.isEmpty(paramMap.get("limit"))
                ? 10 : Integer.parseInt((String) paramMap.get("limit"));
        DepartmentQueryVo departmentQueryVo = new DepartmentQueryVo();
        departmentQueryVo.setHoscode(hoscode);
        //3签名校验（省略）
        Page<Department>  pageModel =
                departmentService.selectPage(page,limit,departmentQueryVo);
        return Results.ok(pageModel);

    }

    @ApiOperation(value = "删除科室")
    @PostMapping("department/remove")
    public Results removeDepartment(HttpServletRequest request) {
        //1获取参数进行封装
        Map<String, String[]> parameterMap = request.getParameterMap();
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(parameterMap);
        //2获取参数
        String hoscode = (String) paramMap.get("hoscode");
        String depcode = (String) paramMap.get("depcode");
        //3签名校验（省略）
        departmentService.remove(hoscode,depcode);
        return Results.ok();
    }


    @ApiOperation(value = "上传排班")
    @PostMapping("saveSchedule")
    public Results saveSchedule(HttpServletRequest request) {
        //1获取参数进行封装
        Map<String, String[]> parameterMap = request.getParameterMap();
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(parameterMap);
        //2关键参数校验（省略）
        //3签名校验（省略）
        //4保存排班
        scheduleService.save(paramMap);
        return Results.ok();
    }

}
