package kr.hhplus.be.server.domain.event.order;

import kr.hhplus.be.server.domain.event.DomainEvent;
import kr.hhplus.be.server.domain.vo.order.OrderId;

import java.util.UUID;

public record OrderPlaced(
        UUID uuid,
        OrderId orderId
) implements DomainEvent {}
