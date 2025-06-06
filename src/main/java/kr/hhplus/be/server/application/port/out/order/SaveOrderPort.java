package kr.hhplus.be.server.application.port.out.order;

import kr.hhplus.be.server.domain.model.Order;

public interface SaveOrderPort {
    Order saveOrder(Order order);
}
