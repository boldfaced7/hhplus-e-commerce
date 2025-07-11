package kr.hhplus.be.server.domain.event.order;

import kr.hhplus.be.server.domain.event.DomainEvent;
import kr.hhplus.be.server.domain.vo.coupon.CouponId;
import kr.hhplus.be.server.domain.vo.order.OrderDiscountTotalPrice;
import kr.hhplus.be.server.domain.vo.order.OrderId;
import kr.hhplus.be.server.domain.vo.orderitem.OrderItemQuantities;
import kr.hhplus.be.server.domain.vo.user.UserId;

import java.util.UUID;

public record OrderCreated(
        UUID uuid,
        OrderId orderId,
        UserId userId,
        CouponId couponId,
        OrderDiscountTotalPrice price,
        OrderItemQuantities quantities
) implements DomainEvent {}
