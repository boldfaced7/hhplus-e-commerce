package kr.hhplus.be.server.application.service.saga.order;

import kr.hhplus.be.server.domain.vo.order.Guid;
import kr.hhplus.be.server.domain.vo.order.OrderId;

public record CompleteOrderCommand(
        Guid guid,
        OrderId orderId
) {
}