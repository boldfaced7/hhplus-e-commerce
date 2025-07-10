package kr.hhplus.be.server.application.service.saga.balance;

import kr.hhplus.be.server.application.service.saga.balance.impl.CancelBalanceDeductionServiceImpl;
import kr.hhplus.be.server.domain.model.Balance;
import kr.hhplus.be.server.application.support.ApplicationUnitTestSupport;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import java.util.Optional;

import static kr.hhplus.be.server.application.service.TestConstants.*;
import static kr.hhplus.be.server.application.service.saga.SagaTestUtils.setUpBalance;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CancelBalanceDeductionServiceTest extends ApplicationUnitTestSupport {

    @InjectMocks CancelBalanceDeductionServiceImpl cancelBalanceDeductionService;

    @Test
    void 잔액_차감_취소_성공() {
        // given
        Balance balance = spy(setUpBalance(BALANCE_AMOUNT_DEDUCTED));
        stubBalanceDeductionCancel(balance, true);
        
        // when
        cancelBalanceDeductionService.cancelBalanceDeduction(CANCEL_BALANCE_DEDUCTION_COMMAND);
        
        // then
        verifyBalanceDeductionCancel(balance, true);
    }

    @Test
    void 잔액_차감_취소_실패() {
        // given
        stubBalanceDeductionCancel(null, false);
        
        // when & then
        assertThatThrownBy(() -> {
            cancelBalanceDeductionService.cancelBalanceDeduction(CANCEL_BALANCE_DEDUCTION_COMMAND);
        }).isInstanceOf(RuntimeException.class);
        verifyBalanceDeductionCancel(null, false);
    }

    void stubBalanceDeductionCancel(Balance balance, boolean succeeded) {
        // 잔액 조회
        when(loadBalancePort.loadBalanceForUpdate(USER_ID)).thenReturn(Optional.ofNullable(balance));
        if (succeeded) {
            // 잔액 차감 취소 업데이트
            when(updateBalancePort.updateBalance(balance)).thenReturn(balance);
            // 이벤트 발행
            doNothing().when(publishEventPort).publishEvents(any(Balance.class));
        }
    }

    void verifyBalanceDeductionCancel(Balance balance, boolean succeeded) {
        // 잔액 조회
        verify(loadBalancePort).loadBalanceForUpdate(USER_ID);

        if (balance == null) return;

        if (succeeded) {
            // 잔액 차감 취소
            verify(balance).restore(UUID_RANDOM, ORDER_DISCOUNT_PRICE);
            // 잔액 차감 취소 업데이트
            verify(updateBalancePort).updateBalance(balance);
            // 이벤트 발행
            verify(publishEventPort).publishEvents(any(Balance.class));
        }
    }

}