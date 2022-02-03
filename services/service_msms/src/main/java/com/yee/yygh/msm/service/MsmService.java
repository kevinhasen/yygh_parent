package com.yee.yygh.msm.service;

import com.yee.yygh.vo.msm.MsmVo;

/**
 * ClassName: MsmService
 * Description:
 * date: 2022/1/4 15:15
 *
 * @author Yee
 * @since JDK 1.8
 */
public interface MsmService {
    //发送短信
    boolean send(String phone, String code);
    //发送短信接口
    boolean send(MsmVo msmVo);

}
