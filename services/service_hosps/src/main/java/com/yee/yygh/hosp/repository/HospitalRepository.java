package com.yee.yygh.hosp.repository;

import com.yee.yygh.model.hosp.Hospital;
import com.yee.yygh.vo.hosp.DepartmentVo;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * ClassName: HospitalRepository
 * Description:
 * date: 2021/12/28 13:56
 * 上传医院接口
 * @author Yee
 * @since JDK 1.8
 */
public interface HospitalRepository extends MongoRepository<Hospital,String> {
    //根据hoscode查询mongo有没有医院信息
    Hospital getByHoscode(String hoscode);
    //根据医院名称获取医院列表
    List<Hospital> findByHosnameLike(String hosname);
    //获取科室列表
//    List<DepartmentVo> findByHoscode(String hoscode);
    
}
