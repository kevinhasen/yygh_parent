package com.yee.yygh.msm.service.impl;

import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.yee.yygh.common.handler.YyghException;
import com.yee.yygh.common.result.ResultCodeEnum;
import com.yee.yygh.msm.service.MsmService;
import com.yee.yygh.msm.util.RandomUtil;
import com.yee.yygh.vo.msm.MsmVo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;


/**
 * ClassName: MsmServiceImpl
 * Description:
 * date: 2022/1/4 15:16
 * 新版的阿里云核心版本4.5.16
 * @author Yee
 * @since JDK 1.8
 */
@Service
public class MsmServiceImpl implements MsmService {

    @Value("${aliyunSms.accessKeyId}")
    private String accessKeyId;
    @Value("${aliyunSms.secret}")
    private String secret;
    @Value("${aliyunSms.SignName}")
    private String SignName;
    @Value("${aliyunSms.TemplateCode}")
    private String TemplateCode;
//    发送短信
    @Override
    public boolean send(String phone, String code) {
        //判断手机号
        if (StringUtils.isEmpty(phone))return false;
        //第一个参数是哪个区,这里填深圳,第二个是开发者key,第三个是value
        DefaultProfile profile = DefaultProfile.getProfile("cn-shenzhen", accessKeyId, secret);

        IAcsClient client = new DefaultAcsClient(profile);

        CommonRequest request = new CommonRequest();
        request.setSysMethod(MethodType.POST);
        request.setSysDomain("dysmsapi.aliyuncs.com");
        request.setSysVersion("2017-05-25");
        request.setSysAction("SendSms");
        //必填:待发送手机号
        request.putQueryParameter("PhoneNumbers", phone);
        // 必填:短信签名-可在短信控制台中找到
        request.putQueryParameter("SignName", SignName);
        // 必填:短信模板-可在短信控制台中找到
        request.putQueryParameter("TemplateCode", TemplateCode);
        //随机生成的验证码
        request.putQueryParameter("TemplateParam", "{\"code\":\""+code+"\"}");
        try {
            CommonResponse response = client.getCommonResponse(request);
            System.out.println(response.getData());
            return response.getHttpResponse().isSuccess();
        } catch (ServerException e) {
            e.printStackTrace();
            throw new YyghException(20001, ResultCodeEnum.CODE_ERROR.getMessage());
        } catch (ClientException e) {
            e.printStackTrace();
            throw new YyghException(20001, ResultCodeEnum.CODE_ERROR.getMessage());
        }
    }

    //发送短信接口
    @Override
    public boolean send(MsmVo msmVo) {
        //1获取参数
        String phone = msmVo.getPhone();
        String templateCode = msmVo.getTemplateCode();
        Map<String, Object> param = msmVo.getParam();
        //2模拟发送通知短信（发送验证码）
        String code = RandomUtil.getFourBitRandom();
        boolean isSend = this.send(phone, code);
        return isSend;
    }
}
