package com.yee.yygh.hosp.service;

import com.yee.yygh.model.hosp.Hospital;
import com.yee.yygh.vo.hosp.DepartmentVo;
import com.yee.yygh.vo.hosp.HospitalQueryVo;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;


/**
 * ClassName: HospitalService
 * Description:
 * date: 2021/12/28 13:55
 * 医院信息医院列表等
 * @author Yee
 * @since JDK 1.8
 */
public interface HospitalService {
    //上传医院
    void saveHospital(Map<String, Object> paramMap);
    //获取医院信息
    Hospital selectHosp(String hoscode);
    //带条件分页查询医院列表
    Page<Hospital> selectPage(Integer page, Integer limit, HospitalQueryVo hospitalQueryVo);
    //更新上线状态
    void lock(String id, Integer status);
    //获取医院详情
    Map<String, Object> getHospById(String id);
    //根据医院编码获取医院名称
    String getHospName(String hoscode);
    //根据医院名称获取医院列表
    List<Hospital> findByHosname(String hosname);
    //医院预约挂号详情
    Map<String, Object> getHospByHoscode(String hoscode);
}
