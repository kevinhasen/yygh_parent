package com.yee.yygh.hosp.service;

import com.yee.yygh.model.hosp.Department;
import com.yee.yygh.vo.hosp.DepartmentQueryVo;
import com.yee.yygh.vo.hosp.DepartmentVo;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

/**
 * ClassName: DepartmentService
 * Description:
 * date: 2021/12/28 20:12
 * 科室信息
 * @author Yee
 * @since JDK 1.8
 */
public interface DepartmentService {
    //上传科室
    void save(Map<String, Object> paramMap);
    //带条件、带分页查询科室
    Page<Department> selectPage(int page, int limit, DepartmentQueryVo departmentQueryVo);
    //删除科室
    void remove(String hoscode, String depcode);
    //查询医院所有科室列表
    List<DepartmentVo> findDeptTree(String hoscode);
    //根据医院编码、科室编码获取科室名称
    String getDepName(String hoscode, String depcode);
    //根据医院编码、科室编码获取科室信息
    Department getDepartment(String hoscode, String depcode);
}
