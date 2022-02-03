package com.yee.yygh.sta.controller;

import com.yee.yygh.common.result.Result;
import com.yee.yygh.user.client.OrderFeignClient;
import com.yee.yygh.vo.order.OrderCountQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * ClassName: StatisticsController
 * Description:
 * date: 2022/1/16 18:57
 *
 * @author Yee
 * @since JDK 1.8
 */
@Api(tags = "统计管理接口")
@RestController
@RequestMapping("/admin/statistics")
public class StatisticsController {
    @Autowired
    private OrderFeignClient orderFeignClient;

    @ApiOperation( "获取订单统计数据")
    @GetMapping("/getCountMap")
    public Result getCountMap(OrderCountQueryVo orderCountQueryVo){
        Map<String, Object> map = orderFeignClient.getCountMap(orderCountQueryVo);
        return Result.ok().data(map);
    }
}
