package kr.hhplus.be.server.application.service.saga.coupon;

import kr.hhplus.be.server.application.service.saga.coupon.impl.UseCouponServiceImpl;
import kr.hhplus.be.server.domain.model.Coupon;
import kr.hhplus.be.server.application.support.ApplicationUnitTestSupport;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import java.util.Optional;

import static kr.hhplus.be.server.application.service.TestConstants.*;
import static kr.hhplus.be.server.application.service.saga.SagaTestUtils.setUpCoupon;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UseCouponServiceTest extends ApplicationUnitTestSupport {

    @InjectMocks UseCouponServiceImpl useCouponService;

    @Test
    void 쿠폰_사용_성공() {
        // given
        Coupon coupon = spy(setUpCoupon(COUPON_NOT_USED));
        stubCouponUse(coupon, true);
        
        // when
        useCouponService.useCoupon(USE_COUPON_COMMAND);
        
        // then
        verifyCouponUse(coupon, true);
    }

    @Test
    void 쿠폰_사용_실패() {
        // given
        Coupon coupon = spy(setUpCoupon(COUPON_USED));
        stubCouponUse(coupon, false);
        
        // when & then
        assertThatThrownBy(() -> {
            useCouponService.useCoupon(USE_COUPON_COMMAND);
        }).isInstanceOf(RuntimeException.class);
        verifyCouponUse(coupon, false);
    }

    @Test
    void 쿠폰_조회_실패() {
        // given
        stubCouponUse(null, false);
        
        // when & then
        assertThatThrownBy(() -> {
            useCouponService.useCoupon(USE_COUPON_COMMAND);
        }).isInstanceOf(RuntimeException.class);
        verifyCouponUse(null, false);
    }

    void stubCouponUse(Coupon coupon, boolean isSucceeded) {
        // 쿠폰 조회
        when(loadCouponPort.loadCouponForUpdate(COUPON_ID)).thenReturn(Optional.ofNullable(coupon));
        if (isSucceeded) {
            // 쿠폰 사용 업데이트
            when(updateCouponPort.updateCoupon(coupon)).thenReturn(coupon);
        }
        // 이벤트 발행
        doNothing().when(publishEventPort).publishEvents(any(Coupon.class));
    }

    void verifyCouponUse(Coupon coupon, boolean isSucceeded) {
        verify(loadCouponPort).loadCouponForUpdate(COUPON_ID);
        if (coupon == null) return;

        // 쿠폰 사용
        verify(coupon).use(GUID);

        if (isSucceeded) {
            // 쿠폰 사용 업데이트
            verify(updateCouponPort).updateCoupon(coupon);
        }
        // 이벤트 발행
        verify(publishEventPort).publishEvents(any(Coupon.class));
    }

}