package com.yee.yygh.hosp.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yee.yygh.common.result.Result;
import com.yee.yygh.hosp.service.HospitalSetService;
import com.yee.yygh.model.hosp.HospitalSet;
import com.yee.yygh.vo.hosp.HospitalSetQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ClassName: HospitalSetController
 * Description:
 * date: 2021/12/20 17:57
 *
 * @author Yee
 * @since JDK 1.8
 */
@RestController
@RequestMapping("/admin/hosp/hospitalSet")
//@CrossOrigin  网关统一配置跨域
//医院设置接口
@Api(tags = "医院设置接口")
public class HospitalSetController {
    @Autowired
    private HospitalSetService hospitalSetService;

    //模拟登录
    @PostMapping("/login")
    @ApiOperation("模拟登录")
    public Result login(){
        return Result.ok().data("token","admin-token");
    }

    //模拟获取用户信息
    @GetMapping("/info")
    @ApiOperation("模拟获取用户信息")
    public Result info(){
        Map<String,Object> map = new HashMap<>();
        map.put("admin","admin");
        map.put("introduction","这个人很懒没有签名");
        map.put("avatar","https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif");
        map.put("name","超级管理员");
        return Result.ok().data(map);
    }

    //查询所有医院设置
    @GetMapping("/findAll")
    @ApiOperation("医院设置列表")
    public Result getHospitalSetList(){

        List<HospitalSet> list = hospitalSetService.list();
        return Result.ok().data("list",list);
    }

    @DeleteMapping("/{id}")
    @ApiOperation("医院设置删除")
    public Result removeById(@ApiParam(name = "id", value = "要删除的id", required = true)
                                  @PathVariable("id") Long id){
        boolean remove = hospitalSetService.removeById(id);
        if (remove){
            return Result.ok();
        }else {
            return Result.error();
        }
    }

    @ApiOperation("简单分页医院设置列表")
    @GetMapping("/{page}/{limit}")
    public Result pageList( @ApiParam(name = "page", value = "当前页码", required = true)
                            @PathVariable("page") Long page,
                            @ApiParam(name = "limit", value = "每页记录数", required = true)
                           @PathVariable("limit") Long limit){
        Page<HospitalSet> pages = new Page<>(page,limit);
        hospitalSetService.page(pages);
        List<HospitalSet> list = pages.getRecords();
        long pagesTotal = pages.getTotal();
        return Result.ok().data("list",list).data("pagesTotal",pagesTotal);
    }
    @ApiOperation("带条件分页查询")
    @PostMapping("/pageQuery/{page}/{limit}")
    public Result pageQuery(@ApiParam(name = "page", value = "当前页码", required = true)
                                @PathVariable("page") Long page,
                            @ApiParam(name = "limit", value = "每页记录数", required = true)
                                @PathVariable("limit") Long limit,
                            @ApiParam(name = "hospitalSetQueryVo", value = "查询对象", required = false)
                                @RequestBody(required = false) HospitalSetQueryVo hospitalSetQueryVo){
        //1取出查询条件，判断是否为空
        String hosname = hospitalSetQueryVo.getHosname();
        String hoscode = hospitalSetQueryVo.getHoscode();
        QueryWrapper<HospitalSet> wrapper = new QueryWrapper<>();
        if (!StringUtils.isEmpty(hosname)){
            wrapper.like("hosname",hosname);
        }
        if (!StringUtils.isEmpty(hoscode)){
            wrapper.eq("hoscode",hoscode);
        }
        //2实现分页查询
        Page<HospitalSet> pageParam = new Page<>(page,limit);
        hospitalSetService.page(pageParam,wrapper);
        List<HospitalSet> list = pageParam.getRecords();
        long total = pageParam.getTotal();
        return Result.ok().data("list",list).data("total",total);
    }

    @ApiOperation( "新增医院设置")
    @PostMapping("/saveHosp")
    public Result saveHosp(@ApiParam(name = "hospitalSet",value = "请求体",required = true)
                               @RequestBody(required = true)HospitalSet hospitalSet){
        boolean save = hospitalSetService.save(hospitalSet);
        if (save){
            return Result.ok();
        }else {
            return Result.error();
        }
    }

    @ApiOperation("根据id查询医院设置")
    @GetMapping("/{id}")
    public Result getHospById(
            @ApiParam(name = "id", value = "根据id查询医院", required = true)
            @PathVariable("id") Long id){
        HospitalSet hospitalSet = hospitalSetService.getById(id);
        return Result.ok().data("hospitalSet",hospitalSet);
    }


    @ApiOperation("修改医院设置")
    @PutMapping("/updateHosp")
    public Result updateHosp(
            @ApiParam(name = "hospitalSet", value = "请求体", required = true)
            @RequestBody HospitalSet hospitalSet){
        boolean update = hospitalSetService.updateById(hospitalSet);
        if (update){
            return Result.ok();
        }else {
            return Result.error();
        }
    }

    @ApiOperation(value = "批量删除医院设置")
    @DeleteMapping("/removeHosp")
    public Result removeHosp(
            @ApiParam(name = "ids", value = "根据id批量删除", required = true)
            @RequestBody List<Long> ids){
        boolean remove = hospitalSetService.removeByIds(ids);
        if (remove){
            return Result.ok();
        }else {
            return Result.error();
        }
    }

    // 医院设置锁定和解锁
    @ApiOperation(value = "医院设置锁定和解锁")
    @PutMapping("/lockHospitalSet/{id}/{status}")
    public Result lockHospitalSet(@ApiParam(name = "id", value = "要修改的id", required = true)
                                      @PathVariable("id") Long id,
                                  @ApiParam(name = "status", value = "要设置的状态", required = true)
                                  @PathVariable("status") Integer status){
        HospitalSet hospitalSet = hospitalSetService.getById(id);
        hospitalSet.setStatus(status);
        boolean update = hospitalSetService.updateById(hospitalSet);
        if (update){
            return Result.ok();
        }else {
            return Result.error();
        }
    }

}
