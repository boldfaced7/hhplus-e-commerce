package kr.hhplus.be.server.domain.vo.balance;

import kr.hhplus.be.server.domain.exception.BalanceInsufficientException;

public record BalanceAmount(Long value) {

    public BalanceAmount add(BalanceAmount amount) {
        return new BalanceAmount(this.value + amount.value);
    }

    public BalanceAmount subtract(BalanceAmount amount) {
        if (this.value < amount.value) {
            throw new BalanceInsufficientException();
        }
        return new BalanceAmount(this.value - amount.value);
    }
}
