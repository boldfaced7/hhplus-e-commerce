package kr.hhplus.be.server.adapter.out.persistence.order;

import kr.hhplus.be.server.adapter.out.persistence.orderitem.OrderItemJpa;
import kr.hhplus.be.server.adapter.out.persistence.orderitem.OrderItemMapper;
import kr.hhplus.be.server.domain.model.Order;
import kr.hhplus.be.server.domain.vo.coupon.CouponId;
import kr.hhplus.be.server.domain.vo.order.OrderDiscountTotalPrice;
import kr.hhplus.be.server.domain.vo.order.OrderId;
import kr.hhplus.be.server.domain.vo.order.OrderOriginalTotalPrice;
import kr.hhplus.be.server.domain.vo.user.UserId;

import java.util.Map;

public class OrderMapper {

    public static OrderJpa toJpa(Order order) {
        return new OrderJpa(
            (order.getOrderId() != null) ? order.getOrderId().value() : null,
            order.getUserId().value(),
            order.getCouponId().value(),
            order.getOriginalTotalPrice().value(),
            order.getDiscountTotalPrice().value(),
            order.getCreatedAt(),
            order.getUpdatedAt()
        );
    }

    public static Order toDomain(OrderJpa orderJpa, Map<Long, OrderItemJpa> orderItems) {
        return Order.create(
            new OrderId(orderJpa.getId()),
            new UserId(orderJpa.getUserId()),
            new CouponId(orderJpa.getCouponId()),
            OrderItemMapper.toDomainMap(orderItems),
            new OrderOriginalTotalPrice(orderJpa.getOriginalTotalPrice()),
            new OrderDiscountTotalPrice(orderJpa.getDiscountTotalPrice()),
            orderJpa.getCreatedAt(),
            orderJpa.getUpdatedAt()
        );
    }
}
