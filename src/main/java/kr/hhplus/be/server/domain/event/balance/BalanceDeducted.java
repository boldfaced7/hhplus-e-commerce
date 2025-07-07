package kr.hhplus.be.server.domain.event.balance;

import kr.hhplus.be.server.domain.event.DomainEvent;
import kr.hhplus.be.server.domain.vo.order.Guid;
import kr.hhplus.be.server.domain.vo.order.OrderDiscountTotalPrice;

public record BalanceDeducted(
        Guid guid,
        OrderDiscountTotalPrice price
) implements DomainEvent {}
