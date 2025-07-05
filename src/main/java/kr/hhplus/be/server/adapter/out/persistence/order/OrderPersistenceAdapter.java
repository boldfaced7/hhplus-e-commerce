package kr.hhplus.be.server.adapter.out.persistence.order;

import kr.hhplus.be.server.adapter.out.persistence.orderitem.OrderItemJpaRepository;
import kr.hhplus.be.server.adapter.out.persistence.orderitem.OrderItemMapper;
import kr.hhplus.be.server.application.port.out.order.LoadOrderPort;
import kr.hhplus.be.server.application.port.out.order.SaveOrderPort;
import kr.hhplus.be.server.application.port.out.order.UpdateOrderPort;
import kr.hhplus.be.server.domain.model.Order;
import kr.hhplus.be.server.domain.vo.order.OrderId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

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
        // 멱등성을 위해 동일한 guid를 가진 주문이 이미 존재하는지 확인
        var existingOrder = orderJpaRepository.findByGuid(order.getGuid().value());
        if (existingOrder.isPresent()) {
            // 이미 존재하는 주문의 OrderItems도 함께 조회
            var existingOrderItems = orderItemJpaRepository.loadOrderItems(existingOrder.get().getId());
            return OrderMapper.toDomain(existingOrder.get(), existingOrderItems);
        }

        var toBeSavedOrderJpa = OrderMapper.toJpa(order);
        var savedOrderJpa = orderJpaRepository.save(toBeSavedOrderJpa);

        var toBeSavedOrderItemsJpa = OrderItemMapper.toJpaMap(order.getOrderItems());
        toBeSavedOrderItemsJpa.values().forEach(orderItem ->
                orderItem.setOrderId(savedOrderJpa.getId())
        );
        var savedOrderItemsJpa = orderItemJpaRepository.saveAll(toBeSavedOrderItemsJpa);

        return OrderMapper.toDomain(savedOrderJpa, savedOrderItemsJpa);
    }

    @Override
    public Order updateOrder(Order order) {
        // 멱등성을 위해 동일한 guid를 가진 주문이 존재하는지 확인
        var existingOrder = orderJpaRepository.findByGuid(order.getGuid().value());
        if (existingOrder.isEmpty()) {
            throw new IllegalArgumentException("Order with guid " + order.getGuid().value() + " does not exist");
        }

        var toBeUpdatedOrder = OrderMapper.toJpa(order);
        var toBeUpdatedOrderItems = OrderItemMapper.toJpaMap(order.getOrderItems());

        var updatedOrder = orderJpaRepository.save(toBeUpdatedOrder);
        var updatedOrderItems = orderItemJpaRepository.saveAll(toBeUpdatedOrderItems);

        return OrderMapper.toDomain(updatedOrder, updatedOrderItems);
    }
}
