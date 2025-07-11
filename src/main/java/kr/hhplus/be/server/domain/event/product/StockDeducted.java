package kr.hhplus.be.server.domain.event.product;

import kr.hhplus.be.server.domain.event.DomainEvent;
import kr.hhplus.be.server.domain.vo.orderitem.OrderItemQuantities;

import java.util.UUID;

public record StockDeducted(
        UUID uuid,
        OrderItemQuantities quantities
) implements DomainEvent {}
