package kr.hhplus.be.server.domain.exception;

public class BalanceUnavailableException extends RuntimeException {
    public BalanceUnavailableException() {
        super("사용할 수 없는 잔액입니다.");
    }
}
