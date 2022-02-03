package com.yee.yygh.hosp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yee.yygh.hosp.mapper.HospitalSetMapper;
import com.yee.yygh.model.hosp.HospitalSet;
import org.springframework.stereotype.Service;

/**
 * ClassName: HospitalSetService
 * Description:
 * date: 2021/12/20 17:58
 *
 * @author Yee
 * @since JDK 1.8
 */
public interface HospitalSetService extends IService<HospitalSet>{
      /* 获取签名key
     * @param hoscode
     * @return
      */
    String getSignKey(String hoscode);
}
