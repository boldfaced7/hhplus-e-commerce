package kr.hhplus.be.server.domain.model;

import kr.hhplus.be.server.domain.event.DomainEvent;
import kr.hhplus.be.server.domain.event.balance.BalanceDeducted;
import kr.hhplus.be.server.domain.event.balance.BalanceDeductionCancelled;
import kr.hhplus.be.server.domain.event.balance.BalanceDeductionFailed;
import kr.hhplus.be.server.domain.exception.BalanceUnavailableException;
import kr.hhplus.be.server.domain.vo.balance.BalanceAmount;
import kr.hhplus.be.server.domain.vo.balance.BalanceId;
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

    public Balance charge(OrderDiscountTotalPrice price) {
        checkBalanceAvailable();
        this.amount = this.amount.add(price);
        return this;
    }

    public Balance deduct(OrderDiscountTotalPrice price) {
        checkBalanceAvailable();
        this.amount = this.amount.subtract(price);
        return this;
    }

    public void checkBalanceAvailable() {
        if (isBalanceUnavailable()) {
            throw new BalanceUnavailableException();
        }
    }

    public boolean isBalanceUnavailable() {
        return balanceId == null && userId == null && amount == null &&
                createdAt == LocalDateTime.MAX && updatedAt == LocalDateTime.MAX;
    }

    public void apply(Consumer<Balance> consumer) {
        consumer.accept(this);
    }

    public void markBalanceDeducted(DomainEvent event) {
        addEvent(new BalanceDeducted(event));
    }

    public void markBalanceDeductionFailed(DomainEvent event) {
        addEvent(new BalanceDeductionFailed(event));
    }

    public void markBalanceDeductionCancelled(DomainEvent event) {
        addEvent(new BalanceDeductionCancelled(event));
    }

}
