package kr.hhplus.be.server.adapter.out.persistence.order;

import kr.hhplus.be.server.adapter.out.persistence.orderitem.OrderItemJpaRepository;
import kr.hhplus.be.server.adapter.out.persistence.orderitem.OrderItemMapper;
import kr.hhplus.be.server.application.exception.OrderAlreadyRequestedException;
import kr.hhplus.be.server.application.port.out.order.LoadOrderPort;
import kr.hhplus.be.server.application.port.out.order.SaveOrderPort;
import kr.hhplus.be.server.application.port.out.order.UpdateOrderPort;
import kr.hhplus.be.server.domain.model.Order;
import kr.hhplus.be.server.domain.vo.order.OrderId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderPersistenceAdapter implements
        LoadOrderPort,
        SaveOrderPort,
        UpdateOrderPort
{
    private final OrderJpaRepository orderJpaRepository;
    private final OrderItemJpaRepository orderItemJpaRepository;

    @Override
    public Optional<Order> loadOrder(OrderId orderId) {
        var foundOrderItems = orderItemJpaRepository.loadOrderItems(orderId.value());

        return orderJpaRepository.findById(orderId.value())
                .map(o -> OrderMapper.toDomain(o, foundOrderItems));
    }

    @Override
    public Order saveOrder(Order order) {
        try {
            var saved = persistOrder(order);
            saved.markOrderCreated();
            return saved;
        } catch (DataIntegrityViolationException e) {
            throw new OrderAlreadyRequestedException();
        }
    }

    @Override
    public Order updateOrder(Order order) {
        return persistOrder(order);
    }

    private Order persistOrder(Order order) {
        var toBePersistedOrder = OrderMapper.toJpa(order);
        var toBePersistedOrderItems = OrderItemMapper.toJpaMap(order.getOrderItems());

        var persistedOrder = orderJpaRepository.save(toBePersistedOrder);
        var persistedOrderItems = orderItemJpaRepository.saveAll(toBePersistedOrderItems);

        return OrderMapper.toDomain(persistedOrder, persistedOrderItems);
    }
}
