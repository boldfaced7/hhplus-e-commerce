package kr.hhplus.be.server.domain.vo.order;

public record OrderSucceeded(
        boolean value
) {
    public static final OrderSucceeded SUCCEEDED = new OrderSucceeded(true);
    public static final OrderSucceeded FAILED = new OrderSucceeded(false);
    public boolean isSucceeded() {
        return value;
    }
}
