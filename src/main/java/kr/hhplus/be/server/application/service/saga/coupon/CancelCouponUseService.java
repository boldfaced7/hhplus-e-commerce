package kr.hhplus.be.server.application.service.saga.coupon;

public interface CancelCouponUseService {
    void cancelCouponUse(CancelCouponUseCommand command);
}
