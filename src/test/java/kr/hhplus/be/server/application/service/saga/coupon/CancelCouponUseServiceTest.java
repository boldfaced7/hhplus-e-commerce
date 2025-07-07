package kr.hhplus.be.server.application.service.saga.coupon;

import kr.hhplus.be.server.application.service.saga.coupon.impl.CancelCouponUseServiceImpl;
import kr.hhplus.be.server.domain.model.Coupon;
import kr.hhplus.be.server.application.support.ApplicationUnitTestSupport;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import java.util.Optional;

import static kr.hhplus.be.server.application.service.TestConstants.*;
import static kr.hhplus.be.server.application.service.saga.SagaTestUtils.setUpCoupon;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CancelCouponUseServiceTest extends ApplicationUnitTestSupport {

    @InjectMocks CancelCouponUseServiceImpl cancelCouponUseService;

    @Test
    void 쿠폰_사용_취소_성공() {
        // given
        Coupon coupon = spy(setUpCoupon(COUPON_USED));
        stubCancelCouponUse(coupon, true);
        
        // when
        cancelCouponUseService.cancelCouponUse(CANCEL_COUPON_USE_COMMAND);
        
        // then
        verifyCancelCouponUse(coupon, true);
    }

    @Test
    void 쿠폰_사용_취소_실패() {
        // given
        stubCancelCouponUse(null, false);
        
        // when & then
        assertThatThrownBy(() -> {
            cancelCouponUseService.cancelCouponUse(CANCEL_COUPON_USE_COMMAND);
        }).isInstanceOf(RuntimeException.class);
        verifyCancelCouponUse(null, false);
    }

    void stubCancelCouponUse(Coupon coupon, boolean isSucceeded) {
        // 쿠폰 조회
        when(loadCouponPort.loadCouponForUpdate(COUPON_ID)).thenReturn(Optional.ofNullable(coupon));
        if (isSucceeded) {
            // 쿠폰 사용 취소 업데이트
            when(updateCouponPort.updateCoupon(coupon)).thenReturn(coupon);
            // 이벤트 발행
            doNothing().when(publishEventPort).publishEvents(any(Coupon.class));
        }
    }

    void verifyCancelCouponUse(Coupon coupon, boolean isSucceeded) {
        // 쿠폰 조회
        verify(loadCouponPort).loadCouponForUpdate(COUPON_ID);
        if (coupon == null) return;

        // 쿠폰 사용 취소
        verify(coupon).restore(GUID);
        // 쿠폰 사용 취소 업데이트
        verify(updateCouponPort).updateCoupon(coupon);
        // 이벤트 발행
        verify(publishEventPort).publishEvents(any(Coupon.class));
    }

}