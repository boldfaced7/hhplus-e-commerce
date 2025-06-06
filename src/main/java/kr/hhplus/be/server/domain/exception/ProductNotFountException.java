package kr.hhplus.be.server.domain.exception;

public class ProductNotFountException extends RuntimeException {
    public ProductNotFountException() {
        super("상품을 찾을 수 없습니다.");
    }

}
