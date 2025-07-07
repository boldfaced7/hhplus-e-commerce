package kr.hhplus.be.server.application.service.saga.order.impl;

import kr.hhplus.be.server.application.port.out.event.PublishEventPort;
import kr.hhplus.be.server.application.port.out.order.LoadOrderPort;
import kr.hhplus.be.server.application.port.out.order.UpdateOrderPort;
import kr.hhplus.be.server.application.service.saga.order.CompleteOrderCommand;
import kr.hhplus.be.server.application.service.saga.order.CompleteOrderService;
import kr.hhplus.be.server.domain.exception.OrderNotFoundException;
import kr.hhplus.be.server.domain.vo.order.OrderId;
import kr.hhplus.be.server.domain.vo.order.OrderSucceeded;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompleteOrderServiceImpl implements CompleteOrderService {

    private final PublishEventPort publishEventPort;
    private final LoadOrderPort loadOrderPort;
    private final UpdateOrderPort updateOrderPort;

    @Override
    @Transactional
    public void succeedOrder(CompleteOrderCommand command) {
        completeOrder(command.orderId(), OrderSucceeded.SUCCEEDED);
    }

    @Override
    @Transactional
    public void failOrder(CompleteOrderCommand command) {
        completeOrder(command.orderId(), OrderSucceeded.FAILED);
    }

    private void completeOrder(
            OrderId orderId,
            OrderSucceeded orderSucceeded
    ) {
        var order = loadOrderPort.loadOrder(orderId)
                .orElseThrow(OrderNotFoundException::new);

        order.complete(orderSucceeded);
        publishEventPort.publishEvents(order);

        updateOrderPort.updateOrder(order);
    }
}
