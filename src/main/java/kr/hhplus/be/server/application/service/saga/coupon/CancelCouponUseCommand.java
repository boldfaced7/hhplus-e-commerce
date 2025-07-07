package kr.hhplus.be.server.application.service.saga.coupon;

import kr.hhplus.be.server.domain.vo.coupon.CouponId;
import kr.hhplus.be.server.domain.vo.order.Guid;

public record CancelCouponUseCommand(
        Guid guid,
        CouponId couponId
) {
}
