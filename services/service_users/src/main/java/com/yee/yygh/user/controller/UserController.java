package com.yee.yygh.user.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yee.yygh.common.result.Result;
import com.yee.yygh.model.user.UserInfo;
import com.yee.yygh.user.service.UserInfoService;
import com.yee.yygh.vo.user.UserInfoQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * ClassName: UserController
 * Description:
 * date: 2022/1/9 0:09
 *
 * @author Yee
 * @since JDK 1.8
 */
@Api(tags = "认证管理")
@RestController
@RequestMapping("/admin/user")
public class UserController {

    @Autowired
    private UserInfoService userInfoService;

    @ApiOperation("条件查询带分页用户列表")
    @GetMapping("/{page}/{limit}")
    public Result list(@PathVariable Long page,
                       @PathVariable Long limit,
                       UserInfoQueryVo userInfoQueryVo) {
        Page<UserInfo> pageParam = new Page<>(page, limit);
        IPage<UserInfo> pageModel =
                userInfoService.selectPage(pageParam, userInfoQueryVo);

        return Result.ok().data("pageModel", pageModel);
    }

    @ApiOperation(value = "锁定")
    @GetMapping("/lock/{userId}/{status}")
    public Result lock(@PathVariable("userId") Long userId,
                       @PathVariable("status") Integer status) {
        userInfoService.lock(userId, status);
        return Result.ok();
    }

    //用户详情
    @ApiOperation(value = "用户详情")
    @GetMapping("/show/{userId}")
    public Result show(@PathVariable Long userId){
        Map<String,Object> map = userInfoService.show(userId);
        return Result.ok().data(map);
    }

    //认证审批
    @ApiOperation(value = "认证审批")
    @GetMapping("/approval/{userId}/{authStatus}")
    public Result approval(@PathVariable Long userId,
                           @PathVariable Integer authStatus){
        userInfoService.approval(userId,authStatus);
        return Result.ok();
    }
}
