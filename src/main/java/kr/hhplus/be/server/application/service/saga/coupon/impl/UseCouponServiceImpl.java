package kr.hhplus.be.server.application.service.saga.coupon.impl;

import kr.hhplus.be.server.application.port.aop.DistributedLock;
import kr.hhplus.be.server.application.port.out.coupon.LoadCouponPort;
import kr.hhplus.be.server.application.port.out.coupon.UpdateCouponPort;
import kr.hhplus.be.server.application.port.out.event.PublishEventPort;
import kr.hhplus.be.server.application.service.saga.coupon.UseCouponCommand;
import kr.hhplus.be.server.application.service.saga.coupon.UseCouponService;
import kr.hhplus.be.server.domain.model.Coupon;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UseCouponServiceImpl implements UseCouponService {

    private final PublishEventPort publishEventPort;
    private final LoadCouponPort loadCouponPort;
    private final UpdateCouponPort updateCouponPort;

    @Override
    @DistributedLock(prefix = "coupon:lock:", key = "#command.couponId")
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void useCoupon(UseCouponCommand command) {
        var loaded = loadCouponPort.loadCouponForUpdate(command.couponId())
                .orElse(Coupon.unavailable()); // 조회 결과가 없는 경우, 사용 불가 쿠폰 사용

        log.info("{}", loaded);
        useCoupon(loaded, command.uuid());
    }

    private void useCoupon(Coupon coupon, UUID uuid) {
        try {
            coupon.use(uuid); // 쿠폰 사용, 실패 시 예외 발생
            updateCouponPort.updateCoupon(coupon); // 쿠폰 업데이트

        } finally {
            publishEventPort.publishEvents(coupon); // 이벤트 발행
        }
    }

}
