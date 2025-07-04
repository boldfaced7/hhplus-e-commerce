package kr.hhplus.be.server.domain.exception;

import kr.hhplus.be.server.domain.vo.coupon.CouponId;

public class CouponAlreadyUsedException extends RuntimeException {
    public CouponAlreadyUsedException() {
        super("이미 사용된 쿠폰입니다.");
    }

    public CouponAlreadyUsedException(CouponId couponId) {
        super("이미 사용된 쿠폰입니다. " + couponId.toString());
    }

}
