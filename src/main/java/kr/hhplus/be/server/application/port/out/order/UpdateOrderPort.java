package kr.hhplus.be.server.application.port.out.order;

import kr.hhplus.be.server.domain.model.Order;

public interface UpdateOrderPort {
    Order saveOrder(Order order);
}
