package kr.hhplus.be.server.domain.vo.order;

public record OrderFinished(
        boolean value
) {
    public static final OrderFinished FINISHED = new OrderFinished(true);
    public static final OrderFinished NOT_FINISHED = new OrderFinished(false);

    public boolean isFinished() {
        return value;
    }
}
