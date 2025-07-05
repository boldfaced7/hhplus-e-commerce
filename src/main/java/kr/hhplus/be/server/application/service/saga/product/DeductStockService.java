package kr.hhplus.be.server.application.service.saga.product;

import kr.hhplus.be.server.domain.event.DomainEvent;

public interface DeductStockService {
    void deductStock(DomainEvent event);
}
