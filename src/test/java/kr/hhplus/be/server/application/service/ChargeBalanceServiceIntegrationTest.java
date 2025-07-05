package kr.hhplus.be.server.application.service;

import kr.hhplus.be.server.application.port.in.ChargeBalanceCommand;
import kr.hhplus.be.server.application.port.out.balance.SaveBalancePort;
import kr.hhplus.be.server.domain.model.Balance;
import kr.hhplus.be.server.application.support.TestContainerSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static kr.hhplus.be.server.application.service.TestConstants.*;
import static org.assertj.core.api.Assertions.assertThat;

class ChargeBalanceServiceIntegrationTest extends TestContainerSupport {
    @Autowired ChargeBalanceService chargeBalanceService;

    @Autowired SaveBalancePort saveBalancePort;

    Balance balance;
    ChargeBalanceCommand command;

    @Test
    @DisplayName("기존 잔액이 있는 경우 충전이 정상적으로 이루어진다.")
    void chargeBalanceWithExistingBalance() {
        // given
        setUpTestData();

        // when
        var chargedBalance = chargeBalanceService.chargeBalance(command);

        // then
        assertThat(chargedBalance.getUserId()).isEqualTo(USER_ID);
        assertThat(chargedBalance.getAmount()).isEqualTo(BALANCE_AMOUNT_ADDED);
    }

    void setUpTestData() {
        balance = saveBalancePort.saveBalance(Balance.create(
                USER_ID,
                BALANCE_AMOUNT
        ));
        command = new ChargeBalanceCommand(USER_ID, BALANCE_AMOUNT);
    }

}