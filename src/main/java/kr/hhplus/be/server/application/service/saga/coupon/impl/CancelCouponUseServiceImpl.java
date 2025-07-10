package kr.hhplus.be.server.application.service.saga.coupon.impl;

import kr.hhplus.be.server.application.port.aop.DistributedLock;
import kr.hhplus.be.server.application.port.out.coupon.LoadCouponPort;
import kr.hhplus.be.server.application.port.out.coupon.UpdateCouponPort;
import kr.hhplus.be.server.application.port.out.event.PublishEventPort;
import kr.hhplus.be.server.application.service.saga.coupon.CancelCouponUseCommand;
import kr.hhplus.be.server.application.service.saga.coupon.CancelCouponUseService;
import kr.hhplus.be.server.domain.exception.CouponNotFoundException;
import kr.hhplus.be.server.domain.model.Coupon;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CancelCouponUseServiceImpl implements CancelCouponUseService {

    private final PublishEventPort publishEventPort;
    private final LoadCouponPort loadCouponPort;
    private final UpdateCouponPort updateCouponPort;

    @Override
    @DistributedLock(prefix = "coupon:lock:", key = "#command.couponId")
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void cancelCouponUse(CancelCouponUseCommand command) {
        var loaded = loadCouponPort.loadCouponForUpdate(command.couponId())
                .orElseThrow(CouponNotFoundException::new);

        cancelCouponUse(loaded, command.uuid());
    }

    private void cancelCouponUse(Coupon coupon, UUID uuid) {
        coupon.restore(uuid); // 쿠폰 사용 취소
        updateCouponPort.updateCoupon(coupon); // 쿠폰 업데이트
        publishEventPort.publishEvents(coupon); // 이벤트 발행
    }

}
