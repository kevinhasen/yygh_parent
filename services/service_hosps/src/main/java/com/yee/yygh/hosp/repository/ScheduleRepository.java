package com.yee.yygh.hosp.repository;

import com.yee.yygh.model.hosp.Schedule;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Date;
import java.util.List;

/**
 * ClassName: ScheduleRepository
 * Description:
 * date: 2021/12/28 21:05
 * 排版信息
 * @author Yee
 * @since JDK 1.8
 */
public interface ScheduleRepository extends MongoRepository<Schedule,String> {
    //根据参数查询排班
    Schedule getByHoscodeAndHosScheduleId(String hoscode, String hosScheduleId);
    //根据医院编号 、科室编号和工作日期，查询排班详细信息
    List<Schedule> getByHoscodeAndDepcodeAndWorkDate(String hoscode, String depcode, Date toDate);
}
