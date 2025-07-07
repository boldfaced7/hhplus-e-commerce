package kr.hhplus.be.server.domain.event.product;

import kr.hhplus.be.server.domain.event.DomainEvent;
import kr.hhplus.be.server.domain.vo.order.Guid;
import kr.hhplus.be.server.domain.vo.orderitem.OrderItemQuantities;

public record StockDeductionCancelled(
        Guid guid,
        OrderItemQuantities quantities
) implements DomainEvent {}
