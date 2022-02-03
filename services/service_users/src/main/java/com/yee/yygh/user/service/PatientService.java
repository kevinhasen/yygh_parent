package com.yee.yygh.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yee.yygh.model.user.Patient;

import java.util.List;

/**
 * ClassName: PatientService
 * Description:
 * date: 2022/1/7 21:24
 *
 * @author Yee
 * @since JDK 1.8
 */
public interface PatientService extends IService<Patient> {
//    获取就诊人列表
    List<Patient> findAll(Long userId);

//    根据id获取就诊人信息
    Patient getPatientById(Long id);
}
