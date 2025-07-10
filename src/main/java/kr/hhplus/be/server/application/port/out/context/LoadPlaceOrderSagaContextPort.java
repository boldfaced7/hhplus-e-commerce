package kr.hhplus.be.server.application.port.out.context;

import kr.hhplus.be.server.application.service.saga.PlaceOrderSagaContext;

import java.util.Optional;
import java.util.UUID;

public interface LoadPlaceOrderSagaContextPort {
    Optional<PlaceOrderSagaContext> loadByUuid(UUID uuid);
}
