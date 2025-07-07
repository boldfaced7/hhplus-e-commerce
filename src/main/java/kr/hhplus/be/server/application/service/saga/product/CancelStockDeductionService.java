package kr.hhplus.be.server.application.service.saga.product;

public interface CancelStockDeductionService {
    void cancelStockDeduction(CancelStockDeductionCommand command);
}
