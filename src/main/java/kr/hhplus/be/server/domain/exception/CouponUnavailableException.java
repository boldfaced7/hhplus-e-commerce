package kr.hhplus.be.server.domain.exception;

public class CouponUnavailableException extends RuntimeException {
    public CouponUnavailableException() {
        super("사용할 수 없는 쿠폰입니다.");
    }
}
