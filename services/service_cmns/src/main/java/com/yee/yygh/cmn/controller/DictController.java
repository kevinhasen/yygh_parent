package com.yee.yygh.cmn.controller;

import com.yee.yygh.cmn.service.DictService;
import com.yee.yygh.common.result.Result;
import com.yee.yygh.model.cmn.Dict;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * ClassName: DictController
 * Description:
 * date: 2021/12/25 10:26
 * 夸模块查询,也不需要封装result,字符串返回即可
 * PathVariable里value夸模块不能省略
 * @author Yee
 * @since JDK 1.8
 */
@Api(tags = "数据字典接口")
@RestController
@RequestMapping("/admin/cmn/dict")
//@CrossOrigin  网关统一配置跨域
public class DictController {
    @Autowired
    private DictService dictService;

    //根据数据id查询子数据列表
    @ApiOperation(value = "查询子数据列表")
    @GetMapping("/findChildData/{id}")
    public Result findChildData(@PathVariable long id){
        List<Dict> list = dictService.findChildData(id);
        return Result.ok().data("list",list);
    }

    @ApiOperation(value="导出")
    @GetMapping(value = "/exportData")
    public void exportData(HttpServletResponse response){
        dictService.exportData(response);
    }

    @ApiOperation(value="导入")
    @PostMapping(value = "/importData")
    public Result importData(MultipartFile file){
        dictService.importData(file);
        return Result.ok();
    }

    //跨模块PathVariable里value夸模块不能省
    //医院自定义字段需要指定医院编码,
    @ApiOperation(value = "获取数据字典名称")
    @GetMapping(value = "/getName/{parentDictCode}/{value}")
    public String getName(@PathVariable("parentDictCode") String  parentDictCode,
                          @PathVariable("value") String value){
        return dictService.getName(parentDictCode, value);
    }

    //跨模块PathVariable里value夸模块不能省略
    //全国统一字段只需要唯一值即可
    @ApiOperation(value = "获取数据字典名称")
    @GetMapping(value = "/getName/{value}")
    public String getName(@PathVariable("value") String value){
        return dictService.getName("",value);
    }

    @ApiOperation(value = "根据dictCode获取下级节点")
    @GetMapping(value = "/findByDictCode/{dictCode}")
    public Result findByDictCode(@PathVariable String dictCode){
        List<Dict> list = dictService.findByDictCode(dictCode);
        return Result.ok().data("list",list);
    }
}
