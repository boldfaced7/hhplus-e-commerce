package kr.hhplus.be.server.domain.event.order;

import kr.hhplus.be.server.domain.event.DomainEvent;
import kr.hhplus.be.server.domain.vo.order.Guid;
import kr.hhplus.be.server.domain.vo.order.OrderId;

public record OrderPlaced(
        Guid guid,
        OrderId orderId
) implements DomainEvent {}
