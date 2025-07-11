package kr.hhplus.be.server.application.service.saga.balance;

import kr.hhplus.be.server.application.service.saga.balance.impl.DeductBalanceServiceImpl;
import kr.hhplus.be.server.domain.model.Balance;
import kr.hhplus.be.server.application.support.ApplicationUnitTestSupport;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import java.util.Optional;

import static kr.hhplus.be.server.application.service.TestConstants.*;
import static kr.hhplus.be.server.application.service.saga.SagaTestUtils.setUpBalance;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DeductBalanceServiceTest extends ApplicationUnitTestSupport {

    @InjectMocks DeductBalanceServiceImpl deductBalanceService;

    @Test
    void 잔액_차감_성공() {
        // given
        Balance balance = spy(setUpBalance(BALANCE_AMOUNT));
        stubBalanceDeduction(balance, true);
        
        // when
        deductBalanceService.deductBalance(DEDUCT_BALANCE_COMMAND);
        
        // then
        verifyBalanceDeduction(balance, true);
    }

    @Test
    void 잔액_차감_실패() {
        // given
        Balance balance = spy(setUpBalance(BALANCE_AMOUNT_INSUFFICIENT));
        stubBalanceDeduction(balance, false);
        
        // when & then
        assertThatThrownBy(() -> {
            deductBalanceService.deductBalance(DEDUCT_BALANCE_COMMAND);
        }).isInstanceOf(RuntimeException.class);
        verifyBalanceDeduction(balance, false);
    }

    @Test
    void 잔액_조회_실패() {
        // given
        stubBalanceDeduction(null, false);
        
        // when & then
        assertThatThrownBy(() -> {
            deductBalanceService.deductBalance(DEDUCT_BALANCE_COMMAND);
        }).isInstanceOf(RuntimeException.class);
        verifyBalanceDeduction(null, false);
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
        // 잔액 조회
        verify(loadBalancePort).loadBalanceForUpdate(USER_ID);
        if (balance == null) return;

        // 잔액 차감
        verify(balance).deduct(UUID_RANDOM, ORDER_DISCOUNT_PRICE);

        if (isSucceeded) {
            // 잔액 차감 업데이트
            verify(updateBalancePort).updateBalance(balance);
        }
        // 이벤트 발행
        verify(publishEventPort).publishEvents(any(Balance.class));
    }


}