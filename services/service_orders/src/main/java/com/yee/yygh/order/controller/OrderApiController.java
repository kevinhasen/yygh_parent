package com.yee.yygh.order.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yee.yygh.common.result.Result;
import com.yee.yygh.common.util.AuthContextHolder;
import com.yee.yygh.enums.OrderStatusEnum;
import com.yee.yygh.model.order.OrderInfo;
import com.yee.yygh.order.service.OrderService;
import com.yee.yygh.vo.order.OrderCountQueryVo;
import com.yee.yygh.vo.order.OrderQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Api(tags = "订单接口")
@RestController
@RequestMapping("/api/order/orderInfo")
public class OrderApiController {
    @Autowired
    private OrderService orderService;

    @ApiOperation(value = "创建订单")
    @PostMapping("auth/submitOrder/{scheduleId}/{patientId}")
    public Result submitOrder(@PathVariable String scheduleId,
                              @PathVariable Long patientId) {
        Long orderId = orderService.submitOrder(scheduleId,patientId);
        return Result.ok().data("orderId",orderId);
    }

    //订单列表（条件查询带分页）
    @ApiOperation(value = "带分页带条件订单列表查询")
    @GetMapping("auth/{page}/{limit}")
    public Result list(@PathVariable Long page,
                       @PathVariable Long limit,
                       OrderQueryVo orderQueryVo, HttpServletRequest request){
        Long userId = AuthContextHolder.getUserId(request);
        orderQueryVo.setUserId(userId);
        IPage<OrderInfo> pageParam = new Page<>(page,limit);
        IPage<OrderInfo> pageModel = orderService.selectPage(pageParam,orderQueryVo);
        return Result.ok().data("pageModel",pageModel);
    }

    @ApiOperation(value = "获取订单状态")
    @GetMapping("auth/getStatusList")
    public Result getStatusList() {
        return Result.ok().data("statusList", OrderStatusEnum.getStatusList());
    }


    //根据订单id查询订单详情
    @ApiOperation(value = "获取订单详情")
    @GetMapping("auth/getOrders/{orderId}")
    public Result getOrders(@PathVariable Long orderId) {
        OrderInfo orderInfo = orderService.getOrderById(orderId);
        return Result.ok().data("orderInfo",orderInfo);
    }

    @ApiOperation(value = "取消预约")
    @GetMapping("auth/cancelOrder/{orderId}")
    public Result  cancelOrder(
            @PathVariable("orderId") Long orderId) {
        Boolean flag = orderService.cancelOrder(orderId);
        return Result.ok().data("flag",flag);
    }

    @ApiOperation(value = "获取订单统计数据")
    @PostMapping("inner/getCountMap")
    public Map<String, Object> getCountMap(
            @RequestBody OrderCountQueryVo orderCountQueryVo) {
        Map<String,Object> map = orderService.getCountMap(orderCountQueryVo);
        return map;
    }

}
