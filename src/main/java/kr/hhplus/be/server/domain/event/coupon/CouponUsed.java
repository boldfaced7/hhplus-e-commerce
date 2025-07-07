package kr.hhplus.be.server.domain.event.coupon;

import kr.hhplus.be.server.domain.event.DomainEvent;
import kr.hhplus.be.server.domain.vo.coupon.CouponId;
import kr.hhplus.be.server.domain.vo.order.Guid;

public record CouponUsed(
        Guid guid,
        CouponId couponId
) implements DomainEvent {}
