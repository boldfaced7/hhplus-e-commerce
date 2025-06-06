package kr.hhplus.be.server.domain.exception;

public class ProductInsufficientException extends RuntimeException {
    public ProductInsufficientException() {
        super("재고가 부족합니다.");
    }
}
