package kr.hhplus.be.server.application.service.saga.coupon;

import kr.hhplus.be.server.application.service.saga.coupon.impl.CancelCouponUseServiceImpl;
import kr.hhplus.be.server.domain.model.Coupon;
import kr.hhplus.be.server.application.support.ApplicationUnitTestSupport;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;

import java.util.Optional;
import java.util.stream.Stream;

import static kr.hhplus.be.server.application.service.TestConstants.*;
import static kr.hhplus.be.server.application.service.saga.SagaTestUtils.setUpCoupon;
import static org.mockito.Mockito.*;

class CancelCouponUseServiceTest extends ApplicationUnitTestSupport {

    @InjectMocks CancelCouponUseServiceImpl cancelCouponUseService;

    @MethodSource
    @ParameterizedTest(name = "[쿠폰 사용 취소] {0}")
    void 쿠폰_사용_취소(String message, Coupon coupon, boolean isSucceeded) {
        stubCancelCouponUse(coupon, isSucceeded);      // given
        cancelCouponUseService.cancelCouponUse(EVENT); // when
        verifyCancelCouponUse(coupon, isSucceeded);    // then
    }

    static Stream<Arguments> 쿠폰_사용_취소() {
        return Stream.of(
                Arguments.of("성공", spy(setUpCoupon(COUPON_USED)), true),
                Arguments.of("실패", null, false)
        );
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
        int times = isSucceeded ? 1 : 0;

        // 쿠폰 조회
        verify(loadCouponPort).loadCouponForUpdate(COUPON_ID);
        if (coupon != null) {
            // 쿠폰 사용 취소
            verify(coupon).restore();
            // 이벤트 생성
            verify(coupon).markCouponUseCancelled(EVENT);
        }
        // 쿠폰 사용 취소 업데이트
        verify(updateCouponPort, times(times)).updateCoupon(coupon);
        // 이벤트 발행
        verify(publishEventPort, times(times)).publishEvents(coupon);
    }

}