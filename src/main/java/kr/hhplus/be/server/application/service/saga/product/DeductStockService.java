package kr.hhplus.be.server.application.service.saga.product;

public interface DeductStockService {
    void deductStock(DeductStockCommand command);
}
