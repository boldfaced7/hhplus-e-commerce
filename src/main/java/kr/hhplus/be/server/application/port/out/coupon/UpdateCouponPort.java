package kr.hhplus.be.server.application.port.out.coupon;

import kr.hhplus.be.server.domain.model.Coupon;

public interface UpdateCouponPort {
    Coupon updateCoupon(Coupon coupon);
}