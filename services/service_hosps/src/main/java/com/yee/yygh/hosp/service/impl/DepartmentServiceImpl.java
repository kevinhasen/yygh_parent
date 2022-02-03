package com.yee.yygh.hosp.service.impl;


import com.alibaba.fastjson.JSONObject;
import com.yee.yygh.common.handler.YyghException;
import com.yee.yygh.common.result.ResultCodeEnum;
import com.yee.yygh.hosp.repository.DepartmentRepository;
import com.yee.yygh.hosp.service.DepartmentService;
import com.yee.yygh.model.hosp.Department;
import com.yee.yygh.model.hosp.Hospital;
import com.yee.yygh.vo.hosp.DepartmentQueryVo;

import com.yee.yygh.vo.hosp.DepartmentVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;

import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * ClassName: DepartmentServiceImpl
 * Description:
 * date: 2021/12/28 20:13
 *上传医院接口
 * @author Yee
 * @since JDK 1.8
 */
@Service
public class DepartmentServiceImpl implements DepartmentService {
    @Autowired
    private DepartmentRepository departmentRepository;

    //上传科室
    @Override
    public void save(Map<String, Object> paramMap) {
        //1转化参数类型
        String toJSONString = JSONObject.toJSONString(paramMap);
        Department department = JSONObject.parseObject(toJSONString, Department.class);
        //2查询mongo确认是否有此科室
        Department departmentExist = departmentRepository
                .getByHoscodeAndDepcode(department.getHoscode(),department.getDepcode());

        //3有科室更新数据，没科室进行新增
        if(departmentExist!=null){
            department.setId(departmentExist.getId());
            department.setCreateTime(departmentExist.getCreateTime());
            department.setUpdateTime(new Date());
            department.setIsDeleted(0);
            departmentRepository.save(department);
        }else {
            department.setCreateTime(new Date());
            department.setUpdateTime(new Date());
            department.setIsDeleted(0);
            departmentRepository.save(department);
        }

    }
    //带条件、带分页查询科室
    @Override
    public Page<Department> selectPage(int page, int limit, DepartmentQueryVo queryVo) {
        //创建排序对象
        Sort sort = Sort.by(Sort.Direction.DESC,"createTime");
        //创建分页对象 0为第一页
        Pageable pageable = PageRequest.of((page - 1), limit);
        //查询条件构造,字符串不区分大小写,模糊匹配
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                .withIgnoreCase(true);
        //查询条件
        Department department = new Department();
        //转换查询条件
        BeanUtils.copyProperties(queryVo,department);
        //查询模板
        Example<Department> example = Example.of(department,matcher);
        //查询科室
        Page<Department> page1 = departmentRepository.findAll(example,pageable);
        return page1;
    }

    //删除科室
    @Override
    public void remove(String hoscode, String depcode) {
        Department department = departmentRepository.getByHoscodeAndDepcode(hoscode, depcode);
        if (department == null){
            throw new YyghException(20001, ResultCodeEnum.FAIL.getMessage());
        }
        departmentRepository.deleteById(department.getId());
    }

    //根据医院编号，查询医院所有科室列表
    @Override
    public List<DepartmentVo> findDeptTree(String hoscode) {
        //创建list集合，用于最终数据封装
        List<DepartmentVo> result = new ArrayList<>();

        //根据医院编号，查询医院所有科室信息
        Department departmentQuery = new Department();
        departmentQuery.setHoscode(hoscode);
        Example example = Example.of(departmentQuery);
        //所有科室列表 departmentList
        List<Department> departmentList = departmentRepository.findAll(example);

        //根据大科室编号  bigcode 分组，获取每个大科室里面下级子科室
        Map<String, List<Department>> deparmentMap =
                departmentList.stream().collect(Collectors.groupingBy(Department::getBigcode));
        //遍历map集合 deparmentMap
        for(Map.Entry<String,List<Department>> entry : deparmentMap.entrySet()) {
            //大科室编号
            String bigcode = entry.getKey();
            //大科室编号对应的全局数据
            List<Department> deparment1List = entry.getValue();
            //封装大科室
            DepartmentVo departmentVo1 = new DepartmentVo();
            departmentVo1.setDepcode(bigcode);
            departmentVo1.setDepname(deparment1List.get(0).getBigname());

            //封装小科室
            List<DepartmentVo> children = new ArrayList<>();

            for(Department department: deparment1List) {
                DepartmentVo departmentVo2 =  new DepartmentVo();
                departmentVo2.setDepcode(department.getDepcode());
                departmentVo2.setDepname(department.getDepname());
                //封装到list集合
                children.add(departmentVo2);
            }
            //把小科室list集合放到大科室children里面
            departmentVo1.setChildren(children);
            //放到最终result里面
            result.add(departmentVo1);
        }
        //返回
        return result;
    }

    //根据医院编码、科室编码获取科室名称
    @Override
    public String getDepName(String hoscode, String depcode) {
        Department department = departmentRepository.getByHoscodeAndDepcode(hoscode, depcode);
        if (department == null){
            throw new YyghException(20001, ResultCodeEnum.FAIL.getMessage());
        }
        return department.getDepname();
    }

    //根据医院编码、科室编码获取科室信息
    @Override
    public Department getDepartment(String hoscode, String depcode) {
        Department department = departmentRepository.getByHoscodeAndDepcode(hoscode, depcode);

        return department;
    }


}
