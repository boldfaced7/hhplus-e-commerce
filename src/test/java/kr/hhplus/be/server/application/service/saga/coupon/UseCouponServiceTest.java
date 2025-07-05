package kr.hhplus.be.server.application.service.saga.coupon;

import kr.hhplus.be.server.application.service.saga.coupon.impl.UseCouponServiceImpl;
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

class UseCouponServiceTest extends ApplicationUnitTestSupport {

    @InjectMocks UseCouponServiceImpl useCouponService;

    @MethodSource
    @ParameterizedTest(name = "[쿠폰 사용] {0}")
    void 쿠폰_사용(String message, Coupon coupon, boolean isSucceeded) {
        stubCouponUse(coupon, isSucceeded);   // given
        useCouponService.useCoupon(EVENT);    // when
        verifyCouponUse(coupon, isSucceeded); // then
    }

    static Stream<Arguments> 쿠폰_사용() {
        return Stream.of(
                Arguments.of("성공", spy(setUpCoupon(COUPON_NOT_USED)), true),
                Arguments.of("실패", spy(setUpCoupon(COUPON_USED)), false),
                Arguments.of("쿠폰 조회 실패", null, false)
        );
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
        int success = isSucceeded ? 1 : 0;
        int failure = isSucceeded ? 0 : 1;

        verify(loadCouponPort).loadCouponForUpdate(COUPON_ID);
        if (coupon != null) {
            // 쿠폰 사용
            verify(coupon).use();
            // 쿠폰 사용 성공 이벤트 생성
            verify(coupon, times(success)).markCouponUsed(EVENT);
            // 쿠폰 사용 실패 이벤트 생성
            verify(coupon, times(failure)).markCouponUseFailed(EVENT);
        }
        // 쿠폰 사용 업데이트
        verify(updateCouponPort, times(success)).updateCoupon(coupon);
        // 이벤트 발행
        verify(publishEventPort).publishEvents(any(Coupon.class));
    }

}