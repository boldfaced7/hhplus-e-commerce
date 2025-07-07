package kr.hhplus.be.server.application.port.out.context;

import kr.hhplus.be.server.application.service.saga.PlaceOrderSagaContext;
import kr.hhplus.be.server.domain.vo.order.Guid;

import java.util.Optional;

public interface LoadPlaceOrderSagaContextPort {
    Optional<PlaceOrderSagaContext> loadByGuid(Guid guid);
}
