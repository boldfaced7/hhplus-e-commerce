package kr.hhplus.be.server.application.service;

import kr.hhplus.be.server.application.port.in.ChargeBalanceCommand;
import kr.hhplus.be.server.domain.model.Balance;
import kr.hhplus.be.server.application.support.ApplicationUnitTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Optional;

import static kr.hhplus.be.server.application.service.TestConstants.BALANCE_AMOUNT;
import static kr.hhplus.be.server.application.service.TestConstants.USER_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ChargeBalanceServiceTest extends ApplicationUnitTestSupport {

    @InjectMocks ChargeBalanceService chargeBalanceService;
    @Mock Balance balance;

    final ChargeBalanceCommand command = new ChargeBalanceCommand(USER_ID, BALANCE_AMOUNT);


    @Test
    @DisplayName("잔액 충전에 성공한다")
    void chargeBalanceSuccess() {
        // given
        // 잔액 조회
        when(loadBalancePort.loadBalanceForUpdate(command.userId())).thenReturn(Optional.of(balance));
        // 잔액 충전
        when(balance.charge(command.amount())).thenReturn(balance);
        // 잔액 업데이트
        when(updateBalancePort.updateBalance(balance)).thenReturn(balance);

        // when
        Balance result = chargeBalanceService.chargeBalance(command);

        // then
        assertThat(result).isNotNull();
        // 잔액 조회 검증
        verify(loadBalancePort).loadBalanceForUpdate(command.userId());
        // 잔액 충전 검증
        verify(balance).charge(command.amount());
        // 잔액 업데이트 검증
        verify(updateBalancePort).updateBalance(balance);
    }
}