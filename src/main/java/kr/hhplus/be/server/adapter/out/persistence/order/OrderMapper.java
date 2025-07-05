package kr.hhplus.be.server.adapter.out.persistence.order;

import kr.hhplus.be.server.adapter.out.persistence.orderitem.OrderItemJpa;
import kr.hhplus.be.server.adapter.out.persistence.orderitem.OrderItemMapper;
import kr.hhplus.be.server.domain.model.Order;
import kr.hhplus.be.server.domain.vo.order.Guid;
import kr.hhplus.be.server.domain.vo.coupon.CouponId;
import kr.hhplus.be.server.domain.vo.order.*;
import kr.hhplus.be.server.domain.vo.user.UserId;

import java.util.Map;

public class OrderMapper {

    public static OrderJpa toJpa(Order order) {
        return new OrderJpa(
            (order.getOrderId() != null) ? order.getOrderId().value() : null,
            order.getGuid().value(),
            order.getUserId().value(),
            order.getCouponId() != null ? order.getCouponId().value() : null,
            order.getOriginalTotalPrice().value(),
            order.getDiscountTotalPrice().value(),
            order.getOrderFinished().value(),
            order.getOrderSucceeded().value(),
            order.getCreatedAt(),
            order.getUpdatedAt()
        );
    }

    public static Order toDomain(OrderJpa orderJpa, Map<Long, OrderItemJpa> orderItems) {
        return Order.create(
            new OrderId(orderJpa.getId()),
            new Guid(orderJpa.getGuid()),
            new UserId(orderJpa.getUserId()),
            orderJpa.getCouponId() != null ? new CouponId(orderJpa.getCouponId()) : null,
            OrderItemMapper.toDomainMap(orderItems),
            new OrderOriginalTotalPrice(orderJpa.getOriginalTotalPrice()),
            new OrderDiscountTotalPrice(orderJpa.getDiscountTotalPrice()),
            new OrderFinished(orderJpa.isOrderFinished()),
            new OrderSucceeded(orderJpa.isOrderSucceeded()),
            orderJpa.getCreatedAt(),
            orderJpa.getUpdatedAt()
        );
    }
}
