package kr.hhplus.be.server.domain.exception;

public class ProductUnavailableException extends RuntimeException {
    public ProductUnavailableException() {
        super("사용할 수 없는 상품입니다.");
    }
}
