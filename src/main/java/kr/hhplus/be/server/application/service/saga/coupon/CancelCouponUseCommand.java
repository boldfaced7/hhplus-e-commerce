package kr.hhplus.be.server.application.service.saga.coupon;

import kr.hhplus.be.server.domain.vo.coupon.CouponId;

import java.util.UUID;

public record CancelCouponUseCommand(
        UUID uuid,
        CouponId couponId
) {
}
