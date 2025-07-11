package kr.hhplus.be.server.application.exception;

public class OrderAlreadyRequestedException extends RuntimeException {
    public OrderAlreadyRequestedException() {
        super("이미 주문이 요청되었습니다.");
    }
}
