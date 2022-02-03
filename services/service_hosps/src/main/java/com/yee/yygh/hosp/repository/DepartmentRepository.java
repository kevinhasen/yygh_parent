package com.yee.yygh.hosp.repository;

import com.yee.yygh.model.hosp.Department;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * ClassName: DepartmentRepository
 * Description:
 * date: 2021/12/28 20:09
 * 上传科室接口
 * @author Yee
 * @since JDK 1.8
 */
public interface DepartmentRepository extends MongoRepository<Department,String> {

    //查询mongo确认是否有此科室
    Department getByHoscodeAndDepcode(String hoscode, String depcode);
}
