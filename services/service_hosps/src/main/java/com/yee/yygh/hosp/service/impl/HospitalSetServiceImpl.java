package com.yee.yygh.hosp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yee.yygh.common.handler.YyghException;
import com.yee.yygh.common.result.ResultCodeEnum;
import com.yee.yygh.hosp.mapper.HospitalSetMapper;
import com.yee.yygh.hosp.service.HospitalSetService;
import com.yee.yygh.model.hosp.HospitalSet;
import org.springframework.stereotype.Service;

/**
 * ClassName: HospitalSetServiceImpl
 * Description:
 * date: 2021/12/20 18:00
 *
 * @author Yee
 * @since JDK 1.8
 */
@Service
public class HospitalSetServiceImpl extends ServiceImpl<HospitalSetMapper, HospitalSet> implements HospitalSetService {
    //获取签名key
    @Override
    public String getSignKey(String hoscode) {
        QueryWrapper<HospitalSet> wrapper = new QueryWrapper<>();
        wrapper.eq("hoscode",hoscode);
        HospitalSet hospitalSet = baseMapper.selectOne(wrapper);
        //判断有没有该医院编码
        if (hospitalSet == null){
            throw  new YyghException(20001, ResultCodeEnum.SIGN_ERROR.getMessage());
        }
        return hospitalSet.getSignKey();
    }
}
