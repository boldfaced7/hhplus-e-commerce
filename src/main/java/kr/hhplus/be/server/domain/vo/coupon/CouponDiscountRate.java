package kr.hhplus.be.server.domain.vo.coupon;

public record CouponDiscountRate(Double value) {
    public static final CouponDiscountRate ZERO = new CouponDiscountRate(0.0);
}
