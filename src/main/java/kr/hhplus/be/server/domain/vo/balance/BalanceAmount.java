package kr.hhplus.be.server.domain.vo.balance;

import kr.hhplus.be.server.domain.exception.BalanceInsufficientException;
import kr.hhplus.be.server.domain.vo.order.OrderDiscountTotalPrice;

public record BalanceAmount(Long value) {

    public BalanceAmount add(BalanceAmount amount) {
        return new BalanceAmount(this.value + amount.value());
    }

    public BalanceAmount add(OrderDiscountTotalPrice amount) {
        return new BalanceAmount(this.value + amount.value());
    }

    public BalanceAmount subtract(OrderDiscountTotalPrice amount) {
        if (this.value < amount.value()) {
            throw new BalanceInsufficientException();
        }
        return new BalanceAmount(this.value - amount.value());
    }

}
