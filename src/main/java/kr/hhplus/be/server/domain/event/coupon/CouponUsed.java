package kr.hhplus.be.server.domain.event.coupon;

import kr.hhplus.be.server.domain.event.DomainEvent;
import kr.hhplus.be.server.domain.vo.coupon.CouponId;

import java.util.UUID;

public record CouponUsed(
        UUID uuid,
        CouponId couponId
) implements DomainEvent {}
