package kr.hhplus.be.server.domain.exception;

public class BalanceInsufficientException extends RuntimeException {
    public BalanceInsufficientException() {
        super("잔액이 부족합니다.");
    }
}
