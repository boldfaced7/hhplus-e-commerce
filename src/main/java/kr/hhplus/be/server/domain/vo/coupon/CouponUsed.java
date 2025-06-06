package kr.hhplus.be.server.domain.vo.coupon;

public record CouponUsed(boolean value) {
    public static final CouponUsed USED = new CouponUsed(true);
    public static final CouponUsed NOT_USED = new CouponUsed(false);
}
