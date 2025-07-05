package kr.hhplus.be.server.application.service.saga.balance;

import kr.hhplus.be.server.application.service.saga.balance.impl.DeductBalanceServiceImpl;
import kr.hhplus.be.server.domain.model.Balance;
import kr.hhplus.be.server.application.support.ApplicationUnitTestSupport;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;

import java.util.Optional;
import java.util.stream.Stream;

import static kr.hhplus.be.server.application.service.TestConstants.*;
import static kr.hhplus.be.server.application.service.saga.SagaTestUtils.setUpBalance;
import static org.mockito.Mockito.*;

class DeductBalanceServiceTest extends ApplicationUnitTestSupport {

    @InjectMocks DeductBalanceServiceImpl deductBalanceService;

    @MethodSource
    @ParameterizedTest(name = "[잔액 차감] {0}")
    void 잔액_차감(String message, Balance balance, boolean isSucceeded) {
        stubBalanceDeduction(balance, isSucceeded);   // given
        deductBalanceService.deductBalance(EVENT);    // when
        verifyBalanceDeduction(balance, isSucceeded); // then
    }

    static Stream<Arguments> 잔액_차감() {
        return Stream.of(
                Arguments.of("성공", spy(setUpBalance(BALANCE_AMOUNT)), true),
                Arguments.of("실패", spy(setUpBalance(BALANCE_AMOUNT_INSUFFICIENT)), false),
                Arguments.of("잔액 조회 실패", null, false)
        );
    }

    void stubBalanceDeduction(Balance balance, boolean isSucceeded) {
        // 잔액 조회
        when(loadBalancePort.loadBalanceForUpdate(USER_ID)).thenReturn(Optional.ofNullable(balance));
        if (isSucceeded) {
            // 잔액 차감 업데이트
            when(updateBalancePort.updateBalance(balance)).thenReturn(balance);
        }
        // 이벤트 발행
        doNothing().when(publishEventPort).publishEvents(any(Balance.class));
    }

    void verifyBalanceDeduction(Balance balance, boolean isSucceeded) {
        int success = isSucceeded ? 1 : 0;
        int failure = isSucceeded ? 0 : 1;

        // 잔액 조회
        verify(loadBalancePort).loadBalanceForUpdate(USER_ID);
        if (balance != null) {
            // 잔액 차감
            verify(balance).deduct(ORDER_DISCOUNT_PRICE);
            // 잔액 차감 성공 이벤트 생성
            verify(balance, times(success)).markBalanceDeducted(EVENT);
            // 잔액 차감 실패 이벤트 생성
            verify(balance, times(failure)).markBalanceDeductionFailed(EVENT);
        }
        // 잔액 차감 업데이트
        verify(updateBalancePort, times(success)).updateBalance(balance);
        // 이벤트 발행
        verify(publishEventPort).publishEvents(any(Balance.class));
    }


}