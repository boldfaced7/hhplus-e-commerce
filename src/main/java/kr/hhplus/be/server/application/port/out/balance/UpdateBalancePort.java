package kr.hhplus.be.server.application.port.out.balance;

import kr.hhplus.be.server.domain.model.Balance;

public interface UpdateBalancePort {
    Balance updateBalance(Balance balance);
}
