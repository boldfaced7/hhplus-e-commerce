package kr.hhplus.be.server.application.service.saga.order;

import kr.hhplus.be.server.domain.vo.order.OrderId;

import java.util.UUID;

public record CompleteOrderCommand(
        UUID uuid,
        OrderId orderId
) {
}