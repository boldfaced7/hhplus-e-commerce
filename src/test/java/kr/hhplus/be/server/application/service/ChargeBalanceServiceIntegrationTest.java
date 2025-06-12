package kr.hhplus.be.server.application.service;

import kr.hhplus.be.server.application.port.in.ChargeBalanceCommand;
import kr.hhplus.be.server.application.port.out.balance.SaveBalancePort;
import kr.hhplus.be.server.domain.model.Balance;
import kr.hhplus.be.server.domain.vo.balance.BalanceAmount;
import kr.hhplus.be.server.domain.vo.user.UserId;
import kr.hhplus.be.server.support.TestContainerSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

class ChargeBalanceServiceIntegrationTest extends TestContainerSupport {
    @Autowired
    private ChargeBalanceService chargeBalanceService;
    
    @Autowired
    private SaveBalancePort saveBalancePort;

    @Test
    @DisplayName("기존 잔액이 있는 경우 충전이 정상적으로 이루어진다.")
    void chargeBalanceWithExistingBalance() {
        // given
        var userId = new UserId(1L);
        var initialAmount = new BalanceAmount(10000L);
        var chargeAmount = new BalanceAmount(5000L);

        var balance = Balance.create(userId, initialAmount);
        balance = saveBalancePort.saveBalance(balance);

        var command = new ChargeBalanceCommand(userId, chargeAmount);

        // when
        var chargedBalance = chargeBalanceService.chargeBalance(command);

        // then
        assertThat(chargedBalance.getUserId().value()).isEqualTo(userId.value());
        assertThat(chargedBalance.getAmount().value()).isEqualTo(initialAmount.value() + chargeAmount.value());
    }

}