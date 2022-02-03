package com.yee.yygh.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yee.yygh.cmn.clients.DictFeignClient;
import com.yee.yygh.enums.DictEnum;
import com.yee.yygh.model.user.Patient;
import com.yee.yygh.user.mapper.PatientMapper;
import com.yee.yygh.user.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * ClassName: PatientServiceImpl
 * Description:
 * date: 2022/1/7 21:24
 *
 * @author Yee
 * @since JDK 1.8
 */
@Service
public class PatientServiceImpl extends ServiceImpl<PatientMapper, Patient> implements PatientService {
    @Autowired
    private DictFeignClient dictFeignClient;
    //    获取就诊人列表
    @Override
    public List<Patient> findAll(Long userId) {
        //userid相当于外键
        QueryWrapper<Patient> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id",userId);
        List<Patient> list = baseMapper.selectList(wrapper);
        //翻译字段
        list.stream().forEach(item -> {
            this.packPatient(item);
        });
        return list;
    }
    //根据id获取就诊人信息
    @Override
    public Patient getPatientById(Long id) {
        Patient patient = baseMapper.selectById(id);
        return this.packPatient(patient);
    }

    //翻译字段
    private Patient  packPatient(Patient patient) {
        //证件类型
        String certificatesType = dictFeignClient.getName(
                DictEnum.CERTIFICATES_TYPE.getDictCode(),
                patient.getCertificatesType());
        //联系人证件类型
        String certificatesStatus =dictFeignClient.getName(
                DictEnum.CERTIFICATES_TYPE.getDictCode(),
                patient.getContactsCertificatesType());
        //省
        String province = dictFeignClient.getName(patient.getProvinceCode());
        //市
        String city = dictFeignClient.getName(patient.getCityCode());
        //区
        String district = dictFeignClient.getName(patient.getDistrictCode());
        patient.getParam().put("certificatesTypeString",certificatesType);
        patient.getParam().put("contactsCertificatesTypeString",certificatesStatus);
        patient.getParam().put("provinceString",province);
        patient.getParam().put("cityString",city);
        patient.getParam().put("districtString",district);
        //省市区拼接
        patient.getParam().put("fullAddress",province+city+district+patient.getAddress());
    return patient;
    }
}
