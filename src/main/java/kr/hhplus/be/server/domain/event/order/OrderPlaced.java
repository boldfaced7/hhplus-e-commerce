package kr.hhplus.be.server.domain.event.order;

import kr.hhplus.be.server.domain.event.DomainEvent;
import kr.hhplus.be.server.domain.model.OrderItems;
import kr.hhplus.be.server.domain.vo.coupon.CouponId;
import kr.hhplus.be.server.domain.vo.order.OrderDiscountTotalPrice;
import kr.hhplus.be.server.domain.vo.order.OrderId;
import kr.hhplus.be.server.domain.vo.order.OrderOriginalTotalPrice;
import kr.hhplus.be.server.domain.vo.orderitem.OrderItemQuantities;
import kr.hhplus.be.server.domain.vo.user.UserId;

import java.time.LocalDateTime;
import java.util.UUID;

public record OrderPlaced(
        UUID uuid,
        OrderId orderId,
        UserId userId,
        CouponId couponId,
        OrderItems orderItems,
        OrderOriginalTotalPrice originalTotalPrice,
        OrderDiscountTotalPrice discountTotalPrice,
        OrderItemQuantities quantities,
        LocalDateTime placedAt
) implements DomainEvent {}
