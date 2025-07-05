package kr.hhplus.be.server.application.service.saga.coupon.impl;

import kr.hhplus.be.server.application.port.out.coupon.LoadCouponPort;
import kr.hhplus.be.server.application.port.out.coupon.UpdateCouponPort;
import kr.hhplus.be.server.application.port.out.event.PublishEventPort;
import kr.hhplus.be.server.application.service.saga.coupon.CancelCouponUseService;
import kr.hhplus.be.server.domain.event.DomainEvent;
import kr.hhplus.be.server.domain.model.Coupon;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CancelCouponUseServiceImpl implements CancelCouponUseService {

    private final PublishEventPort publishEventPort;
    private final LoadCouponPort loadCouponPort;
    private final UpdateCouponPort updateCouponPort;

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void cancelCouponUse(DomainEvent event) {
        loadCouponPort.loadCouponForUpdate(event.couponId())
                .ifPresent(coupon -> cancelCouponUse(coupon, event));
    }

    private void cancelCouponUse(Coupon coupon, DomainEvent event) {
        var cancelled = coupon.restore(); // 쿠폰 사용 취소
        cancelled.markCouponUseCancelled(event); // 쿠폰 사용 취소됨 이벤트 생성

        updateCouponPort.updateCoupon(cancelled); // 쿠폰 업데이트
        publishEventPort.publishEvents(cancelled); // 이벤트 발행
    }

}
