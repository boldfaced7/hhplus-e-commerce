package kr.hhplus.be.server.application.port.in;

import kr.hhplus.be.server.domain.model.Order;

public interface PlaceOrderUseCase {
    Order placeOrder(PlaceOrderCommand command);
}
