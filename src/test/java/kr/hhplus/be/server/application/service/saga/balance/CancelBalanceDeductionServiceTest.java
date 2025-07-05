package kr.hhplus.be.server.application.service.saga.balance;

import kr.hhplus.be.server.application.service.saga.balance.impl.CancelBalanceDeductionServiceImpl;
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

class CancelBalanceDeductionServiceTest extends ApplicationUnitTestSupport {

    @InjectMocks CancelBalanceDeductionServiceImpl cancelBalanceDeductionService;

    @MethodSource
    @ParameterizedTest(name = "[잔액 차감 취소] {0}")
    void 잔액_차감_취소(String message, Balance balance, boolean isSucceeded) {
        stubBalanceDeductionCancel(balance, isSucceeded);            // given
        cancelBalanceDeductionService.cancelBalanceDeduction(EVENT); // when
        verifyBalanceDeductionCancel(balance, isSucceeded);          // then
    }

    static Stream<Arguments> 잔액_차감_취소() {
        return Stream.of(
                Arguments.of("성공", spy(setUpBalance(BALANCE_AMOUNT_DEDUCTED)), true),
                Arguments.of("실패", null, false)
        );
    }

    void stubBalanceDeductionCancel(Balance balance, boolean isSucceeded) {
        // 잔액 조회
        when(loadBalancePort.loadBalanceForUpdate(USER_ID)).thenReturn(Optional.ofNullable(balance));
        if (isSucceeded) {
            // 잔액 차감 취소 업데이트
            when(updateBalancePort.updateBalance(balance)).thenReturn(balance);
            // 이벤트 발행
            doNothing().when(publishEventPort).publishEvents(any(Balance.class));
        }
    }

    void verifyBalanceDeductionCancel(Balance balance, boolean isSucceeded) {
        int times = isSucceeded ? 1 : 0;

        // 잔액 조회
        verify(loadBalancePort).loadBalanceForUpdate(USER_ID);
        if (balance != null) {
            // 잔액 차감 취소
            verify(balance).charge(ORDER_DISCOUNT_PRICE);
            // 이벤트 생성
            verify(balance).markBalanceDeductionCancelled(EVENT);
        }
        // 잔액 차감 취소 업데이트
        verify(updateBalancePort, times(times)).updateBalance(balance);
        // 이벤트 발행
        verify(publishEventPort, times(times)).publishEvents(any(Balance.class));
    }

}