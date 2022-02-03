package com.yee.yygh.user.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yee.yygh.model.user.UserInfo;
import com.yee.yygh.vo.user.LoginVo;
import com.yee.yygh.vo.user.UserAuthVo;
import com.yee.yygh.vo.user.UserInfoQueryVo;

import java.util.Map;


/**
 * ClassName: UserInfoService
 * Description:
 * date: 2022/1/4 10:05
 *
 * @author Yee
 * @since JDK 1.8
 */

public interface UserInfoService extends IService<UserInfo> {
    //会员登录
    Map<String, Object> login(LoginVo loginVo);
//    用户认证接口
    void userAuth(Long userId, UserAuthVo userAuthVo);
//    查询认证信息
    UserInfo getUserInfo(Long userId);
//条件查询带分页用户列表
    IPage<UserInfo> selectPage(Page<UserInfo> pageParam, UserInfoQueryVo userInfoQueryVo);
//锁定
    void lock(Long userId, Integer status);

    //用户详情
    Map<String, Object> show(Long userId);

    //认证审批
    void approval(Long userId, Integer authStatus);
}
