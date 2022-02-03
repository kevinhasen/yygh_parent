package com.yee.yygh.oss.controller;

import com.yee.yygh.common.result.Result;
import com.yee.yygh.oss.service.FileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * ClassName: FileUploadController
 * Description:
 * date: 2022/1/6 16:46
 *
 * @author Yee
 * @since JDK 1.8
 */
@Api(tags="阿里云文件管理")
@RestController
@RequestMapping("/api/oss/file")
public class FileUploadController {
    @Autowired
    private FileService fileService;
/**
 * 文件上传
 */
    @ApiOperation(value = "文件上传")
    @PostMapping("upload")
    public Result upload( @RequestParam("file") MultipartFile file){
        String url = fileService.upload(file);
        return Result.ok().message("文件上传成功")
                .data("url",url);
    }

}
