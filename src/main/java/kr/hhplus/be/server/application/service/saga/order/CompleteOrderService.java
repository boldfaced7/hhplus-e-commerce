package kr.hhplus.be.server.application.service.saga.order;

import kr.hhplus.be.server.domain.vo.order.OrderId;

public interface CompleteOrderService {
    void succeedOrder(OrderId orderId);
    void failOrder(OrderId orderId);
}