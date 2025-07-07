package kr.hhplus.be.server.domain.event.balance;

import kr.hhplus.be.server.domain.event.DomainEvent;
import kr.hhplus.be.server.domain.vo.order.Guid;
import kr.hhplus.be.server.domain.vo.order.OrderDiscountTotalPrice;

public record BalanceDeductionCancelled(
        Guid guid,
        OrderDiscountTotalPrice price
) implements DomainEvent {}
