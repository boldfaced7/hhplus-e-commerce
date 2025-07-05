package kr.hhplus.be.server.application.port.out.coupon;

import kr.hhplus.be.server.domain.model.Coupon;
import kr.hhplus.be.server.domain.vo.coupon.CouponId;

import java.util.Optional;

public interface LoadCouponPort {
    Optional<Coupon> loadCoupon(CouponId couponId);
    Optional<Coupon> loadCouponForUpdate(CouponId couponId);
}
