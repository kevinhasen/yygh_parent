package com.yee.yygh.order.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yee.yygh.model.order.OrderInfo;
import com.yee.yygh.vo.order.OrderCountQueryVo;
import com.yee.yygh.vo.order.OrderCountVo;

import java.util.List;

/**
 * ClassName: OrderInfoMapper
 * Description:
 * date: 2022/1/12 0:04
 *
 * @author Yee
 * @since JDK 1.8
 */
public interface OrderInfoMapper extends BaseMapper<OrderInfo> {
    //统计每天平台预约数据
    List<OrderCountVo> selectOrderCount(OrderCountQueryVo orderCountQueryVo);

}
