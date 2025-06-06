package kr.hhplus.be.server.application.port.in;

import kr.hhplus.be.server.domain.model.Balance;

public interface ChargeBalanceUseCase {
    Balance chargeBalance(ChargeBalanceCommand command);
}
