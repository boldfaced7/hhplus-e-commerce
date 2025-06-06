package kr.hhplus.be.server.domain.exception;

public class BalanceNotFoundException extends RuntimeException {
    public BalanceNotFoundException() {
        super("잔액을 찾을 수 없습니다.");
    }

}
