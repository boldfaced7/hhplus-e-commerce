package kr.hhplus.be.server.application.service.saga.balance;

import kr.hhplus.be.server.domain.event.DomainEvent;

public interface DeductBalanceService {
    void deductBalance(DomainEvent event);
}
