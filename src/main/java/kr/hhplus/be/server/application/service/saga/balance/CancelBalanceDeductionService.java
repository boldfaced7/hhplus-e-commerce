package kr.hhplus.be.server.application.service.saga.balance;

public interface CancelBalanceDeductionService {
    void cancelBalanceDeduction(CancelBalanceDeductionCommand command);
}
