package com.yee.yygh.user.client;

import com.yee.yygh.vo.order.OrderCountQueryVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

/**
 * ClassName: OrderFeignClient
 * Description:
 * date: 2022/1/16 18:49
 *
 * @author Yee
 * @since JDK 1.8
 */
@FeignClient("service-orders")
public interface OrderFeignClient {

    //获取订单统计数据
    @PostMapping("/api/order/orderInfo/inner/getCountMap")
    public Map<String,Object> getCountMap(
            @RequestBody OrderCountQueryVo orderCountQueryVo
    );
}
