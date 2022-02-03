package com.yee.yygh.hosp.service.impl;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yee.yygh.common.handler.YyghException;
import com.yee.yygh.common.result.ResultCodeEnum;
import com.yee.yygh.hosp.repository.ScheduleRepository;
import com.yee.yygh.hosp.service.DepartmentService;
import com.yee.yygh.hosp.service.HospitalService;
import com.yee.yygh.hosp.service.ScheduleService;
import com.yee.yygh.model.hosp.BookingRule;
import com.yee.yygh.model.hosp.Department;
import com.yee.yygh.model.hosp.Hospital;
import com.yee.yygh.model.hosp.Schedule;
import com.yee.yygh.vo.hosp.BookingScheduleRuleVo;
import com.yee.yygh.vo.hosp.ScheduleOrderVo;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * ClassName: ScheduleServiceImpl
 * Description:
 * date: 2021/12/28 21:05
 * 排版信息
 * @author Yee
 * @since JDK 1.8
 */
@Service
public class ScheduleServiceImpl implements ScheduleService {
    @Autowired
    private ScheduleRepository scheduleRepository;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private HospitalService hospitalService;
    @Autowired
    private DepartmentService departmentService;
    //保存排班
    @Override
    public void save(Map<String, Object> paramMap) {
        //1转化参数类型
        String toJSONString = JSONObject.toJSONString(paramMap);
        Schedule schedule = JSONObject.parseObject(toJSONString, Schedule.class);
        //2根据参数查询排班
        Schedule scheduleExist = scheduleRepository
                .getByHoscodeAndHosScheduleId(
                        schedule.getHoscode(),schedule.getHosScheduleId());

        //3有排班进行更新，无排班进行新增
        if(scheduleExist!=null){
            schedule.setId(scheduleExist.getId());
            schedule.setCreateTime(scheduleExist.getCreateTime());
            scheduleExist.setStatus(scheduleExist.getStatus());
            schedule.setUpdateTime(new Date());
            schedule.setIsDeleted(0);
            scheduleRepository.save(schedule);
        }else {
            schedule.setCreateTime(new Date());
            schedule.setUpdateTime(new Date());
            schedule.setIsDeleted(0);
            schedule.setStatus(1);
            scheduleRepository.save(schedule);
        }
    }




    //查询排班规则数据
    @Override
    public Map<String, Object> getScheduleRule(long page, long limit, String hoscode, String depcode) {
        //1 创建返回对象
        Map<String, Object> result = new HashMap<>();
        //2 实现聚合查询，聚合统计、排序、分页（List）
        //2.1 创建查询条件对象
        Criteria criteria = Criteria.where("hoscode").is(hoscode)
                .and("depcode").is(depcode);
        //2.2 创建聚合查询对象
        Aggregation agg = Aggregation.newAggregation(
                //2.3设置筛选条件
                Aggregation.match(criteria),
                //2.4设置聚合条件、统计结果
                Aggregation.group("workDate")
                        .first("workDate").as("workDate")
                        //统计医生数量
                        .count().as("docCount")
                        .sum("reservedNumber").as("reservedNumber")
                        .sum("availableNumber").as("availableNumber"),
                //2.5设置排序
                Aggregation.sort(Sort.Direction.ASC,"workDate"),
                //2.6设置分页
                Aggregation.skip((page-1)*limit),
                Aggregation.limit(limit)
        );
        //2.7实现聚合查询
        AggregationResults<BookingScheduleRuleVo> aggregate
                = mongoTemplate.aggregate(agg, Schedule.class, BookingScheduleRuleVo.class);
        List<BookingScheduleRuleVo> bookingScheduleRuleVoList = aggregate.getMappedResults();
        //3 实现聚合查询总记录数（total）
        //3.1创建总记录数聚合查询对象
        Aggregation aggTotal = Aggregation.newAggregation(
                //2.3设置筛选条件
                Aggregation.match(criteria),
                //2.4设置聚合条件、统计结果
                Aggregation.group("workDate")
        );
        //3.2聚合查询
        AggregationResults<BookingScheduleRuleVo> aggregateTotal
                = mongoTemplate.aggregate(aggTotal, Schedule.class, BookingScheduleRuleVo.class);
        List<BookingScheduleRuleVo> totalList = aggregateTotal.getMappedResults();
        int total = totalList.size();

        //4 根据排班日期计算出周几（借助工具）
        for (BookingScheduleRuleVo bookingScheduleRuleVo : bookingScheduleRuleVoList) {
            Date workDate = bookingScheduleRuleVo.getWorkDate();
            String dayOfWeek = this.getDayOfWeek(new DateTime(workDate));
            bookingScheduleRuleVo.setDayOfWeek(dayOfWeek);
        }

        //5 封装返回数据
        result.put("bookingScheduleRuleList",bookingScheduleRuleVoList);
        result.put("total",total);
        //6 补全数据
        //根据医院编码获取医院名称
        String hosName = hospitalService.getHospName(hoscode);
        Map<String, String> baseMap = new HashMap<>();
        baseMap.put("hosname",hosName);
        result.put("baseMap",baseMap);
        return result;
    }

    //根据医院编号 、科室编号和工作日期，查询排班详细信息
    @Override
    public List<Schedule> getScheduleDetail(String hoscode, String depcode, String workDate) {
        //查询条件要和字段类型匹配,利用工具转为日期类型
        List<Schedule> list = scheduleRepository.getByHoscodeAndDepcodeAndWorkDate(
                hoscode,depcode,new DateTime(workDate).toDate()
        );
        list.stream().forEach(item -> {
            this.packageSchedule(item);
        });
        return list;
    }

    //获取可预约排班数据
    @Override
    public Map<String, Object> getBookingScheduleRule(Integer page, Integer limit,
                                                      String hoscode, String depcode) {
        Map<String, Object> result = new HashMap<>();
        //1、hoscode查询医院信息、获取预约规则
        Hospital hospital = hospitalService.selectHosp(hoscode);
        if(hospital==null){
            throw new YyghException(20001,"医院信息有误");
        }
        BookingRule bookingRule = hospital.getBookingRule();

        //2、根据预约规则、分页信息，获取可以预约的日期集合的分页对象(IPage)
        IPage<Date> iPage = this.getListDate(page,limit,bookingRule);
        List<Date> dateList = iPage.getRecords();

        //3、参考后台接口方法，查询聚合的排班信息
        //3.1 创建查询条件对象
        Criteria criteria = Criteria.where("hoscode").is(hoscode)
                .and("depcode").is(depcode)
                .and("workDate").in(dateList);
        //3.2 创建聚合查询对象
        Aggregation agg = Aggregation.newAggregation(
                //3.3设置筛选条件
                Aggregation.match(criteria),
                //3.4设置聚合条件、统计结果
                Aggregation.group("workDate")
                        .first("workDate").as("workDate")
                        //统计医生数量
                        .count().as("docCount")
                        .sum("reservedNumber").as("reservedNumber")
                        .sum("availableNumber").as("availableNumber")
        );
        //3.5进行聚合查询
        AggregationResults<BookingScheduleRuleVo> aggregate =
                mongoTemplate.aggregate(agg, Schedule.class, BookingScheduleRuleVo.class);
        List<BookingScheduleRuleVo> scheduleVoList = aggregate.getMappedResults();
        //3.6 转化对象scheduleVoList成map，k=workDate，v=BookingScheduleRuleVo
        Map<Date,BookingScheduleRuleVo> scheduleVoMap = new HashMap<>();
        if(!CollectionUtils.isEmpty(scheduleVoList)){
            scheduleVoMap = scheduleVoList.stream().collect(Collectors.toMap(
                    BookingScheduleRuleVo::getWorkDate,
                    BookingScheduleRuleVo->BookingScheduleRuleVo
            ));
        }
        //4、整合两部分数据,(1)预约的日期集合的分页对象 dateList(2)聚合的排班信息scheduleVoMap
        List<BookingScheduleRuleVo> bookingScheduleRuleVoList = new ArrayList<>();
        for (int i = 0,let=dateList.size(); i <let ; i++) {
            //4.1取出每一个可以预约的日期
            Date date = dateList.get(i);
            //4.2根据date从scheduleVoMap取出BookingScheduleRuleVo
            BookingScheduleRuleVo bookingScheduleRuleVo = scheduleVoMap.get(date);
            //4.3如果date当天没有数据，进行初始化
            if(bookingScheduleRuleVo==null){
                bookingScheduleRuleVo = new BookingScheduleRuleVo();
                //出诊医生数0
                bookingScheduleRuleVo.setDocCount(0);
                //科室剩余预约数   -1表示无号
                bookingScheduleRuleVo.setAvailableNumber(-1);
            }
            bookingScheduleRuleVo.setWorkDate(date);
            bookingScheduleRuleVo.setWorkDateMd(date);
            //4.4判断周几
            String dayOfWeek = this.getDayOfWeek(new DateTime(date));
            bookingScheduleRuleVo.setDayOfWeek(dayOfWeek);
            //4.5最后一页、最后一条状态为即将预约
            //  状态 0：正常 1：即将放号 -1：当天已停止挂号
            if(i==let-1&&page==iPage.getPages()){
                bookingScheduleRuleVo.setStatus(1);
            }else{
                bookingScheduleRuleVo.setStatus(0);
            }
            //4.6当天预约如果过了停止挂号时间，不能预约（-1：当天已停止挂号）
            if(i==0&&page==1){
                DateTime stopTime = this.getDateTime(
                        new Date(), bookingRule.getStopTime());
                if(stopTime.isBeforeNow()){
                    bookingScheduleRuleVo.setStatus(-1);
                }
            }
            bookingScheduleRuleVoList.add(bookingScheduleRuleVo);
        }

        //5、数据封装到map里，返回
        result.put("bookingScheduleList",bookingScheduleRuleVoList);
        result.put("total",iPage.getTotal());
        //其他基础数据
        Map<String, String> baseMap = new HashMap<>();
        //医院名称
        baseMap.put("hosname", hospitalService.getHospName(hoscode));
        //科室
        Department department =departmentService.getDepartment(hoscode, depcode);
        //大科室名称
        baseMap.put("bigname", department.getBigname());
        //科室名称
        baseMap.put("depname", department.getDepname());
        //月
        baseMap.put("workDateString", new DateTime().toString("yyyy年MM月"));
        //放号时间
        baseMap.put("releaseTime", bookingRule.getReleaseTime());
        //停号时间
        baseMap.put("stopTime", bookingRule.getStopTime());
        result.put("baseMap", baseMap);
        return result;
    }


    //根据排班id获取排班详情
    @Override
    public Schedule findScheduleById(String id) {
        Schedule schedule = scheduleRepository.findById(id).get();
        //翻译并且返回
        return this.packageSchedule(schedule);
    }

    //根据排班id获取预约下单数据
    @Override
    public ScheduleOrderVo getScheduleOrderVo(String scheduleId) {
        //获得排版信息
        Schedule schedule = this.findScheduleById(scheduleId);
        if (schedule == null){
            throw new YyghException(20001,"排班信息有误");
        }
        //获得医院信息
        Hospital hospital = hospitalService.selectHosp(schedule.getHoscode());
        if(hospital==null){
            throw new YyghException(20001,"医院信息有误");
        }
        //3取出预约规则
        BookingRule bookingRule = hospital.getBookingRule();
        if(bookingRule==null){
            throw new YyghException(20001,"挂号规则有误");
        }
        //4封装数据返回
        ScheduleOrderVo scheduleOrderVo = new ScheduleOrderVo();
        //4.1封装基本数据
        scheduleOrderVo.setHoscode(hospital.getHoscode());
        scheduleOrderVo.setHosname(hospital.getHosname());
        scheduleOrderVo.setDepcode(schedule.getDepcode());
        //根据医院编码、科室编码获取科室信息
        Department department = departmentService.getDepartment(schedule.getHoscode(), schedule.getDepcode());
       //获得科室名称
        scheduleOrderVo.setDepname(department.getDepname());
        //医院自己的排班主键
        scheduleOrderVo.setHosScheduleId(schedule.getHosScheduleId());
        //剩余预约数
        scheduleOrderVo.setAvailableNumber(schedule.getAvailableNumber());
      //职称
        scheduleOrderVo.setTitle(schedule.getTitle());
        //排班日期
        scheduleOrderVo.setReserveDate(schedule.getWorkDate());
        //排班时间
        scheduleOrderVo.setReserveTime(schedule.getWorkTime());
        //挂号费
        scheduleOrderVo.setAmount(schedule.getAmount());
        //退号截止天数（如：就诊前一天为-1，当天为0）
        Integer quitDay = bookingRule.getQuitDay();
        //往前推一天
        DateTime quitDate  = new DateTime(schedule.getWorkDate()).plusDays(quitDay);
       //退号时间
        String quitTime = bookingRule.getQuitTime();
        //转为DateTime可以方便的推时间
        DateTime quitDateTime  = this.getDateTime(quitDate.toDate(), quitTime);
        //退号时间
        scheduleOrderVo.setQuitTime(quitDateTime.toDate());
        //预约开始时间
        DateTime startTime  = this.getDateTime(new Date(), bookingRule.getReleaseTime());
        scheduleOrderVo.setStartTime(startTime.toDate());
        //预约周期
        Date endDate = new DateTime().plusDays(bookingRule.getCycle()).toDate();
        //预约截止时间
        DateTime endTime = this.getDateTime(endDate, bookingRule.getStopTime());
        scheduleOrderVo.setEndTime(endTime.toDate());
        //当天停止挂号时间
        DateTime stopTime  = this.getDateTime(new Date(), bookingRule.getStopTime());
        scheduleOrderVo.setStartTime(startTime.toDate());
        return scheduleOrderVo;
    }

    @Override
    public void update(Schedule schedule) {
        schedule.setUpdateTime(new Date());
        scheduleRepository.save(schedule);
    }


    //根据参数hoscode,hosScheduleId查询排班信息
    @Override
    public Schedule getByHoscodeAndHosScheduleId(String hoscode, String hosScheduleId) {
        Schedule schedule =
                scheduleRepository.getByHoscodeAndHosScheduleId(
                        hoscode,hosScheduleId);
        return schedule;
    }


    //获取可预约的日期集合,返回page对象
    private IPage<Date> getListDate(Integer page, Integer limit, BookingRule bookingRule) {
    //获取当前预约时间,当前日期+预约放号时间
    DateTime releaseTime = this.getDateTime(new Date(),bookingRule.getReleaseTime());
        //预约周期,判断预约周期是否加1,过期之后必须+1
        Integer cycle = bookingRule.getCycle();
        //判断是否时间过期
        if (releaseTime.isBeforeNow()){
            //当前时间过期,末期加1
            cycle+=1;
        }
    //满足周期的日期集合
        List<Date> dateList = new ArrayList<>();
        //小于周期的值都是未过期
        for (Integer i = 0; i < cycle; i++) {
            DateTime plusDays = new DateTime().plusDays(i);
            String dateStr = plusDays.toString("yyyy-MM-dd");
            dateList.add(new DateTime(dateStr).toDate());
        }
        //获取分页信息
        int start = (page-1) * limit;
        int end = (page-1) * limit+limit;
        if (end > dateList.size()){
            end = dateList.size();
        }
        //获取分页后的日期集合
        List<Date> datePage = new ArrayList<>();
        for (int i = start; i < end; i++) {
            datePage.add(dateList.get(i));
        }
        //封装返回
        IPage<Date> iPage = new Page<>(page,limit,dateList.size());
        iPage.setRecords(datePage);
        return iPage;
    }

    /**
     * 将日期转换为datetime
     * 目的是为了里面的方法,可以方便的推时间
     * @param date  当前时间
     * @param releaseTime  几点几分放号
     * @return
     */
    private DateTime getDateTime(Date date, String releaseTime) {
        //拼接当前时间和放号时间
        String dateTimeStr = new DateTime(date).toString("yyyy-MM-dd")+" "+releaseTime;
        //格式化
        DateTime dateTime = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm").parseDateTime(dateTimeStr);
        return dateTime;
    }


    //翻译排班相关字段
    private Schedule  packageSchedule(Schedule schedule) {
        //根据医院编码获取医院名称
        String hospName = hospitalService.getHospName(schedule.getHoscode());
        //根据医院编码、科室编码获取科室名称
        String depname = departmentService.getDepName(schedule.getHoscode(),schedule.getDepcode());
        //获取周几
        String dayOfWeek = this.getDayOfWeek(new DateTime(schedule.getWorkDate()));
        schedule.getParam().put("hosname",hospName);
        schedule.getParam().put("depname",depname);
        schedule.getParam().put("dayOfWeek",dayOfWeek);
        return schedule;

    }

    //固定写法
    private String getDayOfWeek(DateTime dateTime) {
        String dayOfWeek = "";
        switch (dateTime.getDayOfWeek()) {
            case DateTimeConstants.SUNDAY:
                dayOfWeek = "周日";
                break;
            case DateTimeConstants.MONDAY:
                dayOfWeek = "周一";
                break;
            case DateTimeConstants.TUESDAY:
                dayOfWeek = "周二";
                break;
            case DateTimeConstants.WEDNESDAY:
                dayOfWeek = "周三";
                break;
            case DateTimeConstants.THURSDAY:
                dayOfWeek = "周四";
                break;
            case DateTimeConstants.FRIDAY:
                dayOfWeek = "周五";
                break;
            case DateTimeConstants.SATURDAY:
                dayOfWeek = "周六";
            default:
                break;
        }
        return dayOfWeek;

    }
}
