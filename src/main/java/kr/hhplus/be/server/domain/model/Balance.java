package kr.hhplus.be.server.domain.model;

import kr.hhplus.be.server.domain.vo.balance.BalanceAmount;
import kr.hhplus.be.server.domain.vo.balance.BalanceId;
import kr.hhplus.be.server.domain.vo.order.OrderDiscountTotalPrice;
import kr.hhplus.be.server.domain.vo.user.UserId;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Balance {

    private BalanceId balanceId;
    private UserId userId;
    private BalanceAmount amount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static Balance create(
            UserId userId,
            BalanceAmount amount
    ) {
        return new Balance(
                null,
                userId,
                amount,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    public static Balance create(
            BalanceId balanceId,
            UserId userId,
            BalanceAmount amount,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        return new Balance(
                balanceId,
                userId,
                amount,
                createdAt,
                updatedAt
        );
    }

    public void charge(BalanceAmount amount) {
        this.amount = this.amount.add(amount);
    }

    public void deduct(OrderDiscountTotalPrice orderTotalPrice) {
        var totalBalanceAmount = new BalanceAmount(orderTotalPrice.value());
        this.amount = this.amount.subtract(totalBalanceAmount);
    }
}
