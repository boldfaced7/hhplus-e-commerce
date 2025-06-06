package kr.hhplus.be.server.application.port.out.order;

import kr.hhplus.be.server.domain.model.Order;
import kr.hhplus.be.server.domain.vo.order.OrderId;

import java.util.Optional;

public interface LoadOrderPort {
    Optional<Order> loadOrder(OrderId orderId);
}
