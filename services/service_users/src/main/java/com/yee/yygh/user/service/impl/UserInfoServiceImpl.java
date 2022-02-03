package com.yee.yygh.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yee.yygh.common.handler.YyghException;

import com.yee.yygh.common.util.JwtHelper;
import com.yee.yygh.enums.AuthStatusEnum;
import com.yee.yygh.model.user.Patient;
import com.yee.yygh.model.user.UserInfo;
import com.yee.yygh.user.mapper.UserInfoMapper;
import com.yee.yygh.user.service.PatientService;
import com.yee.yygh.user.service.UserInfoService;

import com.yee.yygh.vo.user.LoginVo;
import com.yee.yygh.vo.user.UserAuthVo;
import com.yee.yygh.vo.user.UserInfoQueryVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ClassName: UserInfoServiceImpl
 * Description:
 * date: 2022/1/4 13:51
 *
 * @author Yee
 * @since JDK 1.8
 */
@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements UserInfoService {
    
    @Autowired
    private RedisTemplate<String,String> redisTemplate;
    @Autowired
    private PatientService patientService;
    //会员登录
    @Override
    public Map<String, Object> login(LoginVo loginVo) {
        //1取出手机号验证码，验空
        String phone = loginVo.getPhone();
        String code = loginVo.getCode();
        String openid = loginVo.getOpenid();
        if(StringUtils.isEmpty(phone)||StringUtils.isEmpty(code)){
            throw new YyghException(20001,"登录信息有误");
        }
        //2 校验验证码
        String redisCode = redisTemplate.opsForValue().get(phone);
        if(!code.equals(redisCode)){
            throw new YyghException(20001,"验证码有误");
        }
        Map<String, Object> map = new HashMap<>();
        //2.5判断是否绑定手机号
        if(StringUtils.isEmpty(openid)){
            //3 根据手机号查询用户信息
            QueryWrapper<UserInfo> wrapper = new QueryWrapper<>();
            wrapper.eq("phone",phone);
            UserInfo userInfo = baseMapper.selectOne(wrapper);
            //4 如果用户信息为空，走注册步骤
            if(userInfo==null){
                userInfo = new UserInfo();
                userInfo.setPhone(phone);
                userInfo.setName("");
                userInfo.setStatus(1);
                baseMapper.insert(userInfo);
            }
            //5如果用户信息不为空，判断是否锁定
            if(userInfo.getStatus() == 0) {
                throw new YyghException(20001,"用户已经禁用");
            }


            //6 补全信息

            String name = userInfo.getName();
//            if(StringUtils.isEmpty(name)){
//                name = userInfo.getNickName();
//            }
//            if(StringUtils.isEmpty(name)){
//                name = userInfo.getPhone();
//            }
            name = userInfo.getPhone();
            //7 走登录步骤
            String token = JwtHelper.createToken(userInfo.getId(), name);
            map.put("token",token);
            map.put("name",name);
        }else{
            //8 绑定手机号
            //8.1根据openid查询用户信息
            QueryWrapper<UserInfo> wrapper = new QueryWrapper<>();
            wrapper.eq("openid",openid);
            UserInfo userInfo = baseMapper.selectOne(wrapper);
            if(userInfo==null){
                throw new YyghException(20001,"用户信息有误");
            }
            //8.2绑定手机号，更新数据
            userInfo.setPhone(phone);
            baseMapper.updateById(userInfo);
            //5如果用户信息不为空，判断是否锁定
            if(userInfo.getStatus() == 0) {
                throw new YyghException(20001,"用户已经禁用");
            }


            //6 补全信息

            String name = userInfo.getName();
            if(StringUtils.isEmpty(name)){
                name = userInfo.getNickName();
            }
            if(StringUtils.isEmpty(name)){
                name = userInfo.getPhone();
            }
            //7 走登录步骤
            String token = JwtHelper.createToken(userInfo.getId(), name);
            map.put("token",token);
            map.put("name",name);
        }
        //登录成功,删除验证码
        redisTemplate.delete(phone);
        return map;
    }

    //    用户认证接口
    @Override
    public void userAuth(Long userId, UserAuthVo userAuthVo) {
//根据userId查询用户信息
        UserInfo userInfo = baseMapper.selectById(userId);
        if (userInfo == null){
            throw  new YyghException(20001,"用户信息有误");
        }
        //复制信息
        BeanUtils.copyProperties(userAuthVo,userInfo);
        //设置认证状态
        userInfo.setAuthStatus(AuthStatusEnum.AUTH_RUN.getStatus());
        baseMapper.updateById(userInfo);
    }

//    查询认证信息
    @Override
    public UserInfo getUserInfo(Long userId) {
        //翻译字段
        UserInfo userInfo = baseMapper.selectById(userId);
        if (userInfo == null){
            throw new YyghException(20001,"用户查询不到");
        }
        String statusNameByStatus = AuthStatusEnum.getStatusNameByStatus(userInfo.getAuthStatus());
        userInfo.getParam().put("authStatusString",statusNameByStatus);
        return userInfo;
    }

//    条件查询带分页用户列表
    @Override
    public IPage<UserInfo> selectPage(Page<UserInfo> pageParam, UserInfoQueryVo userInfoQueryVo) {
        //取出查询条件
        String name = userInfoQueryVo.getKeyword();
        Integer status = userInfoQueryVo.getStatus();
        Integer authStatus = userInfoQueryVo.getAuthStatus();
        String createTimeBegin = userInfoQueryVo.getCreateTimeBegin();
        String createTimeEnd = userInfoQueryVo.getCreateTimeEnd();
        //拼写查询条件
        //2、拼写查询条件
        QueryWrapper<UserInfo> wrapper = new QueryWrapper<>();
        if(!StringUtils.isEmpty(name)){
            wrapper.like("name",name);
        }
        if(!StringUtils.isEmpty(status)){
            wrapper.eq("status",status);
        }
        if(!StringUtils.isEmpty(authStatus)) {
            wrapper.eq("auth_status",authStatus);
        }
        if(!StringUtils.isEmpty(createTimeBegin)) {
            wrapper.ge("create_time",createTimeBegin);
        }
        if(!StringUtils.isEmpty(createTimeEnd)) {
            wrapper.le("create_time",createTimeEnd);
        }
//3带条件分页查询
        IPage<UserInfo> pageModel = baseMapper.selectPage(pageParam, wrapper);
        //翻译字段
        pageModel.getRecords().stream().forEach(item -> {
            this.packUserInfo(item);
        });
return pageModel;
    }

    //锁定
    @Override
    public void lock(Long userId, Integer status) {
        if (status == 0 || status == 1){
            UserInfo userInfo = baseMapper.selectById(userId);
            userInfo.setStatus(status);
            this.updateById(userInfo);
        }
    }

    //用户详情
    @Override
    public Map<String, Object> show(Long userId) {
        Map<String, Object> map = new HashMap<>();
        //查询用户信息
        UserInfo userInfo = baseMapper.selectById(userId);
        //翻译字段
        UserInfo info = this.packUserInfo(userInfo);
        //查询就诊人列表
        List<Patient> list = patientService.findAll(userId);
        //封装map，返回
        map.put("userInfo",info);
        map.put("patientList",list);
        return map;
    }

    //认证审批
    @Override
    public void approval(Long userId, Integer authStatus) {
        if (authStatus == 2 || authStatus == -1){
            UserInfo userInfo = baseMapper.selectById(userId);
            userInfo.setAuthStatus(authStatus);
            this.updateById(userInfo);
        }

    }

    //4翻译字段
    private UserInfo packUserInfo(UserInfo userInfo) {
        String statusNameByStatus =
                AuthStatusEnum.getStatusNameByStatus(userInfo.getAuthStatus());
    userInfo.getParam().put("authStatusString",statusNameByStatus);
        //处理用户状态 0  1
        String statusString = userInfo.getStatus().intValue()==0 ?"锁定" : "正常";
        userInfo.getParam().put("statusString",statusString);
        return userInfo;

    }

}
