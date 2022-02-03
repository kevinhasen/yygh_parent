package com.yee.yygh.oss.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * ClassName: FileService
 * Description:
 * date: 2022/1/6 16:45
 *
 * @author Yee
 * @since JDK 1.8
 */
public interface FileService {
    //文件上传阿里云
    String upload(MultipartFile file);
}
