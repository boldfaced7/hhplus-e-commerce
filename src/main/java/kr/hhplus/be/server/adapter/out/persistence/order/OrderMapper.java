package kr.hhplus.be.server.adapter.out.persistence.order;

import kr.hhplus.be.server.adapter.out.persistence.orderitem.OrderItemJpa;
import kr.hhplus.be.server.adapter.out.persistence.orderitem.OrderItemMapper;
import kr.hhplus.be.server.domain.model.Order;
import kr.hhplus.be.server.domain.vo.coupon.CouponId;
import kr.hhplus.be.server.domain.vo.order.OrderDiscountTotalPrice;
import kr.hhplus.be.server.domain.vo.order.OrderId;
import kr.hhplus.be.server.domain.vo.order.OrderOriginalTotalPrice;
import kr.hhplus.be.server.domain.vo.user.UserId;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.stream.Collectors;

public class OrderMapper {

    public static OrderJpa toJpa(Order order) {
        return new OrderJpa(
            (order.getOrderId() != null) ? order.getOrderId().value() : null,
            order.getUserId().value(),
            order.getCouponId() != null ? order.getCouponId().value() : null,
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
            orderJpa.getCouponId() != null ? new CouponId(orderJpa.getCouponId()) : null,
            OrderItemMapper.toDomainMap(orderItems),
            new OrderOriginalTotalPrice(orderJpa.getOriginalTotalPrice()),
            new OrderDiscountTotalPrice(orderJpa.getDiscountTotalPrice()),
            orderJpa.getCreatedAt(),
            orderJpa.getUpdatedAt()
        );
    }
}
