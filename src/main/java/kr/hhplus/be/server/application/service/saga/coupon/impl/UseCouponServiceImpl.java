package kr.hhplus.be.server.application.service.saga.coupon.impl;

import kr.hhplus.be.server.application.port.out.coupon.LoadCouponPort;
import kr.hhplus.be.server.application.port.out.coupon.UpdateCouponPort;
import kr.hhplus.be.server.application.port.out.event.PublishEventPort;
import kr.hhplus.be.server.application.service.saga.coupon.UseCouponService;
import kr.hhplus.be.server.domain.event.DomainEvent;
import kr.hhplus.be.server.domain.model.Coupon;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UseCouponServiceImpl implements UseCouponService {

    private final PublishEventPort publishEventPort;
    private final LoadCouponPort loadCouponPort;
    private final UpdateCouponPort updateCouponPort;

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void useCoupon(DomainEvent event) {
        loadCouponPort.loadCouponForUpdate(event.couponId())
                .orElse(Coupon.unavailable()) // 조회 결과가 없는 경우, 사용 불가 쿠폰 사용
                .apply(coupon -> useCoupon(coupon, event));
    }

    private void useCoupon(Coupon coupon, DomainEvent event) {
        try {
            coupon.use(); // 쿠폰 사용, 실패 시 예외 발생
            coupon.markCouponUsed(event); // 쿠폰 사용 성공 이벤트 생성
            updateCouponPort.updateCoupon(coupon); // 쿠폰 업데이트

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            coupon.markCouponUseFailed(event); // 쿠폰 사용 실패 이벤트 생성
        }
        publishEventPort.publishEvents(coupon); // 이벤트 발행
    }

}
