package kr.hhplus.be.server.domain.event.balance;

import kr.hhplus.be.server.domain.event.DomainEvent;
import kr.hhplus.be.server.domain.vo.order.OrderDiscountTotalPrice;

import java.util.UUID;

public record BalanceDeductionFailed(
        UUID uuid,
        OrderDiscountTotalPrice price
) implements DomainEvent {}
