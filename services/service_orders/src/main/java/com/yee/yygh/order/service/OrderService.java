package com.yee.yygh.order.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yee.yygh.model.order.OrderInfo;
import com.yee.yygh.vo.order.OrderCountQueryVo;
import com.yee.yygh.vo.order.OrderQueryVo;

import java.util.Map;

/**
 * ClassName: OrderService
 * Description:
 * date: 2022/1/12 0:04
 *
 * @author Yee
 * @since JDK 1.8
 */
public interface OrderService extends IService<OrderInfo>{
    //创建订单
    Long submitOrder(String scheduleId, Long patientId);
    //带分页带条件订单列表查询
    IPage<OrderInfo> selectPage(IPage<OrderInfo> pageParam, OrderQueryVo orderQueryVo);
    //获取订单详情
    OrderInfo getOrderById(Long orderId);
    //取消预约
    Boolean cancelOrder(Long orderId);
    /**
     * 就诊提醒
     */
    void patientTips();
    //获取订单统计数据
    Map<String, Object> getCountMap(OrderCountQueryVo orderCountQueryVo);
}
