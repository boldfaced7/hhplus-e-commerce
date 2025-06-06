package kr.hhplus.be.server.application.port.out.coupon;

import kr.hhplus.be.server.domain.model.Coupon;

public interface SaveCouponPort {
    Coupon saveCoupon(Coupon coupon);
}