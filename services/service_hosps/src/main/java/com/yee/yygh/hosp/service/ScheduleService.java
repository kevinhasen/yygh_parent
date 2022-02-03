package com.yee.yygh.hosp.service;


import com.yee.yygh.model.hosp.Schedule;
import com.yee.yygh.vo.hosp.ScheduleOrderVo;
import java.util.List;
import java.util.Map;

/**
 * ClassName: ScheduleService
 * Description:
 * date: 2021/12/28 21:04
 *
 * @author Yee
 * @since JDK 1.8
 */
public interface ScheduleService {

    //保存排班
    void save(Map<String, Object> paramMap);
    //查询排班规则数据
    Map<String, Object> getScheduleRule(long page, long limit, String hoscode, String depcode);
    //根据医院编号 、科室编号和工作日期，查询排班详细信息
    List<Schedule> getScheduleDetail(String hoscode, String depcode, String workDate);
    //获取可预约排班数据
    Map<String, Object> getBookingScheduleRule(Integer page, Integer limit, String hoscode, String depcode);
    //根据排班id获取排班详情
    Schedule findScheduleById(String id);
    //根据排班id获取预约下单数据
    ScheduleOrderVo getScheduleOrderVo(String scheduleId);
    /**
     * 修改排班
     */
    void update(Schedule schedule);
    //根据参数hoscode,hosScheduleId查询排班信息
    Schedule getByHoscodeAndHosScheduleId(String hoscode, String hosScheduleId);
}
