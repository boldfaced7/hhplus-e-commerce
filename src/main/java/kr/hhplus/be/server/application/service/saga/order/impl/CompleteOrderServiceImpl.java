package kr.hhplus.be.server.application.service.saga.order.impl;

import kr.hhplus.be.server.application.port.out.event.PublishEventPort;
import kr.hhplus.be.server.application.port.out.order.LoadOrderPort;
import kr.hhplus.be.server.application.port.out.order.UpdateOrderPort;
import kr.hhplus.be.server.application.service.saga.order.CompleteOrderService;
import kr.hhplus.be.server.domain.exception.OrderNotFoundException;
import kr.hhplus.be.server.domain.model.Order;
import kr.hhplus.be.server.domain.vo.order.OrderId;
import kr.hhplus.be.server.domain.vo.order.OrderSucceeded;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
public class CompleteOrderServiceImpl implements CompleteOrderService {

    private final PublishEventPort publishEventPort;
    private final LoadOrderPort loadOrderPort;
    private final UpdateOrderPort updateOrderPort;

    @Override
    @Transactional
    public void succeedOrder(OrderId orderId) {
        completeOrder(orderId, OrderSucceeded.SUCCEEDED, Order::markOrderPlaced);
    }

    @Override
    @Transactional
    public void failOrder(OrderId orderId) {
        completeOrder(orderId, OrderSucceeded.FAILED, Order::markOrderFailed);
    }

    private void completeOrder(
            OrderId orderId,
            OrderSucceeded orderSucceeded,
            Consumer<Order> eventMarker
    ) {
        var loaded = loadOrderPort.loadOrder(orderId)
                .orElseThrow(OrderNotFoundException::new);

        var completed = loaded.complete(orderSucceeded);
        eventMarker.accept(completed);

        updateOrderPort.updateOrder(completed);
        publishEventPort.publishEvents(completed);
    }
}
