package kr.hhplus.be.server.application.service.saga.order;

public interface CompleteOrderService {
    void succeedOrder(CompleteOrderCommand command);
    void failOrder(CompleteOrderCommand command);
}