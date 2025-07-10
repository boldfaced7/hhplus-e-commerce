package kr.hhplus.be.server.domain.model;

import kr.hhplus.be.server.domain.event.balance.BalanceDeducted;
import kr.hhplus.be.server.domain.event.balance.BalanceDeductionCancelled;
import kr.hhplus.be.server.domain.event.balance.BalanceDeductionFailed;
import kr.hhplus.be.server.domain.exception.BalanceUnavailableException;
import kr.hhplus.be.server.domain.vo.balance.BalanceAmount;
import kr.hhplus.be.server.domain.vo.balance.BalanceId;
import kr.hhplus.be.server.domain.vo.order.OrderDiscountTotalPrice;
import kr.hhplus.be.server.domain.vo.user.UserId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.function.Consumer;

@ToString
@Getter
@AllArgsConstructor
public class Balance extends EventDomain {

    private BalanceId balanceId;
    private UserId userId;
    private BalanceAmount amount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static Balance unavailable() {
        return new Balance(
            null,
            null,
            null,
            LocalDateTime.MAX,
            LocalDateTime.MAX
        );
    }

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

    public Balance charge(BalanceAmount amount) {
        checkBalanceAvailable();
        this.amount = this.amount.add(amount);
        return this;
    }

    public void restore(UUID uuid, OrderDiscountTotalPrice price) {
        checkBalanceAvailable();
        this.amount = this.amount.add(price);
        markBalanceDeductionCancelled(uuid, price);
    }

    public void deduct(UUID uuid, OrderDiscountTotalPrice price) {
        checkBalanceAvailable();
        try {
            this.amount = this.amount.subtract(price);
            markBalanceDeducted(uuid, price);
        } catch (Exception e) {
            markBalanceDeductionFailed(uuid, price);
            throw e;
        }
    }

    private void checkBalanceAvailable() {
        if (isBalanceUnavailable()) {
            throw new BalanceUnavailableException();
        }
    }

    private boolean isBalanceUnavailable() {
        return balanceId == null && userId == null && amount == null &&
                createdAt == LocalDateTime.MAX && updatedAt == LocalDateTime.MAX;
    }

    public void apply(Consumer<Balance> consumer) {
        consumer.accept(this);
    }

    private void markBalanceDeducted(UUID uuid, OrderDiscountTotalPrice price) {
        addEvent(new BalanceDeducted(uuid, price));
    }

    private void markBalanceDeductionFailed(UUID uuid, OrderDiscountTotalPrice price) {
        addEvent(new BalanceDeductionFailed(uuid, price));
    }

    private void markBalanceDeductionCancelled(UUID uuid, OrderDiscountTotalPrice price) {
        addEvent(new BalanceDeductionCancelled(uuid, price));
    }

}
