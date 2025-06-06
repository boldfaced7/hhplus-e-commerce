package kr.hhplus.be.server.application.port.out.balance;

import kr.hhplus.be.server.domain.model.Balance;

public interface SaveBalancePort {
    Balance saveBalance(Balance balance);
}
