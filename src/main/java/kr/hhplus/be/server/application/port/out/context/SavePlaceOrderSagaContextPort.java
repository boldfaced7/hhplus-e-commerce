package kr.hhplus.be.server.application.port.out.context;

import kr.hhplus.be.server.application.service.saga.PlaceOrderSagaContext;

public interface SavePlaceOrderSagaContextPort {
    void save(PlaceOrderSagaContext context);
}
