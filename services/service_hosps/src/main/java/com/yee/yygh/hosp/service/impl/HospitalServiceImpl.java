package com.yee.yygh.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.yee.yygh.cmn.clients.DictFeignClient;
import com.yee.yygh.common.handler.YyghException;
import com.yee.yygh.common.result.ResultCodeEnum;
import com.yee.yygh.enums.DictEnum;
import com.yee.yygh.hosp.repository.HospitalRepository;
import com.yee.yygh.hosp.service.HospitalService;

import com.yee.yygh.model.hosp.BookingRule;
import com.yee.yygh.model.hosp.Hospital;
import com.yee.yygh.vo.hosp.DepartmentVo;
import com.yee.yygh.vo.hosp.HospitalQueryVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;

import java.util.List;
import java.util.Map;


/**
 * ClassName: HospitalServiceImpl
 * Description:
 * date: 2021/12/28 13:57
 *
 * @author Yee
 * @since JDK 1.8
 */
@Service
public class HospitalServiceImpl implements HospitalService {
    //引入MongoDB操作类
    @Autowired
    private HospitalRepository hospitalRepository;
    //注入远程字典接口
    @Autowired
    private DictFeignClient dictFeignClient;

    //根据医院编码获取医院名称
    @Override
    public String getHospName(String hoscode) {
        Hospital hospital = this.selectHosp(hoscode);
        if (hospital == null){
            throw new YyghException(20001, ResultCodeEnum.PARAM_ERROR.getMessage());
        }
        return hospital.getHosname();
    }

    //上传医院
    @Override
    public void saveHospital(Map<String, Object> paramMap) {
        //1paramMap转化Hospital
        String toJSONString = JSONObject.toJSONString(paramMap);
        Hospital hospital = JSONObject.parseObject(toJSONString, Hospital.class);
        //2根据hoscode查询mongo有没有医院信息
        Hospital targetHospital =
                hospitalRepository.getByHoscode(hospital.getHoscode());
        //3有医院信息进行更新，没有新增
        if(targetHospital!=null){
            //更新
            hospital.setId(targetHospital.getId());
            hospital.setStatus(targetHospital.getStatus());
            hospital.setCreateTime(targetHospital.getCreateTime());
            hospital.setUpdateTime(new Date());
            hospital.setIsDeleted(0);
            hospitalRepository.save(hospital);
        }else{
            //新增
            //0：未上线 1：已上线
            hospital.setStatus(0);
            hospital.setCreateTime(new Date());
            hospital.setUpdateTime(new Date());
            hospital.setIsDeleted(0);
            hospitalRepository.save(hospital);
        }
    }

    //获取医院信息
    @Override
    public Hospital selectHosp(String hoscode) {
        return hospitalRepository.getByHoscode(hoscode);
    }

    //带条件分页查询医院列表
    //注意,分页从0开始(page - 1)
    @Override
    public Page<Hospital> selectPage(Integer page, Integer limit, HospitalQueryVo hospitalQueryVo) {
        //创建排序对象
        Sort sort = Sort.by(Sort.Direction.DESC,"createTime");
        //创建分页查询对象
        Pageable pageable = PageRequest.of((page - 1),limit,sort);
        //创建查询条件
        Hospital hospital = new Hospital();
        BeanUtils.copyProperties(hospitalQueryVo,hospital);
        //创建查询构造器,字符串不区分大小写
        ExampleMatcher matcher = ExampleMatcher.matching().
                withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING).
                withIgnoreCase(true);
        Example<Hospital> example = Example.of(hospital,matcher);
        //查询分页
        Page<Hospital> pages  = hospitalRepository.findAll(example, pageable);
        //跨模块调用翻译编码
        pages.getContent().stream().forEach(item -> {
            this.packHospital(item);
        });
        return pages;
    }

//    更新上线状态
    @Override
    public void lock(String id, Integer status) {
        if (status == 0 || status == 1){
            Hospital hospital = hospitalRepository.findById(id).get();
            hospital.setStatus(status);
            //MongoDB需要手动更新时间
            hospital.setUpdateTime(new Date());
            hospitalRepository.save(hospital);
        }
    }

    //获取医院详情
    @Override
    public Map<String, Object> getHospById(String id) {
        //1根据id查询mongo，补全信息
        Hospital hospital = this.packHospital(hospitalRepository.findById(id).get());
        //取出预约规则,单独放
        BookingRule bookingRule = hospital.getBookingRule();
        //不需要放两次规则,清空一个
        hospital.setBookingRule(null);
        //封装数据
        Map<String, Object> result = new HashMap<>();
        //医院基本信息包含等级
        result.put("hospital",hospital);
        result.put("bookingRule", bookingRule);
        return result;
    }

    //根据医院名称获取医院列表
    //通常使用模糊查询
    @Override
    public List<Hospital> findByHosname(String hosname) {
        return hospitalRepository.findByHosnameLike(hosname);
    }



//    医院预约挂号详情
    @Override
    public Map<String, Object> getHospByHoscode(String hoscode) {
        Map<String, Object> map = new HashMap<>();
        Hospital hospital = this.packHospital(hospitalRepository.getByHoscode(hoscode));
        BookingRule bookingRule  = hospital.getBookingRule();
        hospital.setBookingRule(null);
        map.put("hospital", hospital);
        map.put("bookingRule", bookingRule);
        return map;
    }


    //翻译编码
    private Hospital  packHospital(Hospital hospital) {
        //省市区字段翻译
        String provinceCode = hospital.getProvinceCode();
        String provinceString = dictFeignClient.getName(provinceCode);
        String cityString = dictFeignClient.getName(hospital.getCityCode());
        String districtString = dictFeignClient.getName(hospital.getDistrictCode());
        //医院等级翻译
        String hostype = hospital.getHostype();
        String hostypeString =
                dictFeignClient.getName(DictEnum.HOSTYPE.getDictCode(), hostype);
        //数据封装
        hospital.getParam().put("hostypeString", hostypeString);
        hospital.getParam().put("fullAddress", provinceString + cityString + districtString + hospital.getAddress());
        return hospital;

    }
}
