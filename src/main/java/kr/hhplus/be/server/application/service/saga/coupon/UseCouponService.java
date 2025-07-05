package kr.hhplus.be.server.application.service.saga.coupon;

import kr.hhplus.be.server.domain.event.DomainEvent;

public interface UseCouponService {
    void useCoupon(DomainEvent event);
}
