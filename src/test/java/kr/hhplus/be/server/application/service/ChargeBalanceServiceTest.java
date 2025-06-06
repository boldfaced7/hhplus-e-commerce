package kr.hhplus.be.server.application.service;

import kr.hhplus.be.server.application.port.in.ChargeBalanceCommand;
import kr.hhplus.be.server.application.port.out.balance.LoadBalancePort;
import kr.hhplus.be.server.application.port.out.balance.UpdateBalancePort;
import kr.hhplus.be.server.domain.model.Balance;
import kr.hhplus.be.server.domain.vo.balance.BalanceAmount;
import kr.hhplus.be.server.domain.vo.user.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChargeBalanceServiceTest {

    @InjectMocks
    private ChargeBalanceService chargeBalanceService;

    @Mock private Balance balance;
    @Mock private LoadBalancePort loadBalancePort;
    @Mock private UpdateBalancePort updateBalancePort;

    private UserId userId;
    private BalanceAmount amount;
    private ChargeBalanceCommand command;

    @BeforeEach
    void setUp() {
        userId = new UserId(1L);
        amount = new BalanceAmount(10000L);
        command = new ChargeBalanceCommand(userId, amount);
    }

    @Test
    @DisplayName("잔액 충전에 성공한다")
    void chargeBalanceSuccess() {
        // given
        // 잔액 조회
        doReturn(Optional.of(balance)).when(loadBalancePort).loadBalance(userId);
        // 잔액 충전
        doNothing().when(balance).charge(amount);
        // 잔액 업데이트
        doReturn(balance).when(updateBalancePort).updateBalance(balance);

        // when
        Balance result = chargeBalanceService.chargeBalance(command);

        // then
        assertThat(result).isNotNull();
        
        // 잔액 조회 검증
        verify(loadBalancePort).loadBalance(userId);
        // 잔액 충전 검증
        verify(balance).charge(amount);
        // 잔액 업데이트 검증
        verify(updateBalancePort).updateBalance(balance);
    }
}