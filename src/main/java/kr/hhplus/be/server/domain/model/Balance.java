package kr.hhplus.be.server.domain.model;

import kr.hhplus.be.server.domain.event.balance.BalanceDeducted;
import kr.hhplus.be.server.domain.event.balance.BalanceDeductionCancelled;
import kr.hhplus.be.server.domain.event.balance.BalanceDeductionFailed;
import kr.hhplus.be.server.domain.exception.BalanceUnavailableException;
import kr.hhplus.be.server.domain.vo.balance.BalanceAmount;
import kr.hhplus.be.server.domain.vo.balance.BalanceId;
import kr.hhplus.be.server.domain.vo.order.Guid;
import kr.hhplus.be.server.domain.vo.order.OrderDiscountTotalPrice;
import kr.hhplus.be.server.domain.vo.user.UserId;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.function.Consumer;

@ToString
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
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

    public Balance charge(BalanceAmount amount) {
        checkBalanceAvailable();
        this.amount = this.amount.add(amount);
        return this;
    }

    public void restore(Guid guid, OrderDiscountTotalPrice price) {
        checkBalanceAvailable();
        this.amount = this.amount.add(price);
        markBalanceDeductionCancelled(guid, price);
    }

    public void deduct(Guid guid, OrderDiscountTotalPrice price) {
        checkBalanceAvailable();
        try {
            this.amount = this.amount.subtract(price);
            markBalanceDeducted(guid, price);
        } catch (Exception e) {
            markBalanceDeductionFailed(guid, price);
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

    private void markBalanceDeducted(Guid guid, OrderDiscountTotalPrice price) {
        addEvent(new BalanceDeducted(guid, price));
    }

    private void markBalanceDeductionFailed(Guid guid, OrderDiscountTotalPrice price) {
        addEvent(new BalanceDeductionFailed(guid, price));
    }

    private void markBalanceDeductionCancelled(Guid guid, OrderDiscountTotalPrice price) {
        addEvent(new BalanceDeductionCancelled(guid, price));
    }

}
