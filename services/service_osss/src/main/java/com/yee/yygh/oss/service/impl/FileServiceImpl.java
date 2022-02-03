package com.yee.yygh.oss.service.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.yee.yygh.common.handler.YyghException;
import com.yee.yygh.oss.service.FileService;
import com.yee.yygh.oss.util.OssProperties;

import org.joda.time.DateTime;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.UUID;

/**
 * ClassName: FileServiceImpl
 * Description:
 * date: 2022/1/6 16:46
 *
 * @author Yee
 * @since JDK 1.8
 */
@Service
public class FileServiceImpl implements FileService {

    //文件上传至阿里云
    @Override
    public String upload(MultipartFile file) {
        // yourEndpoint填写Bucket所在地域对应的Endpoint。以华东1（杭州）为例，Endpoint填写为https://oss-cn-hangzhou.aliyuncs.com。
        String endpoint = OssProperties.ENDPOINT;
// 阿里云账号AccessKey拥有所有API的访问权限，风险很高。强烈建议您创建并使用RAM用户进行API访问或日常运维，请登录RAM控制台创建RAM用户。
        String accessKeyId = OssProperties.KEYID;
        String accessKeySecret = OssProperties.KEYSECRET;
// 填写Bucket名称，例如examplebucket。
        String bucketName = OssProperties.BUCKETNAME;
// 填写文件名。文件名包含路径，不包含Bucket名称。例如exampledir/exampleobject.txt。
        String uuid = UUID.randomUUID().toString().replace("-","");
        //按照当前日期，创建文件夹，上传到创建文件夹里面
        String timeUrl = new DateTime().toString("yyyy/MM/dd");
        //以日期开头,uuid中间,加文件名结尾
        String fileName = timeUrl+"/"+uuid+file.getOriginalFilename();
        OSS ossClient = null;
        try {
            // 创建OSSClient实例。
            ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
            ossClient.putObject(bucketName, fileName,file.getInputStream());
            //上传之后文件路径
            String url = "https://"+bucketName+"."+endpoint+"/"+fileName;
            return url;
        } catch (IOException e){
            e.printStackTrace();
            throw new YyghException(20001,"上传文件失败");
        } finally {
            // 关闭OSSClient。
            ossClient.shutdown();
        }
    }
}
