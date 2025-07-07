package kr.hhplus.be.server.application.service.saga.balance;

public interface DeductBalanceService {
    void deductBalance(DeductBalanceCommand command);
}
