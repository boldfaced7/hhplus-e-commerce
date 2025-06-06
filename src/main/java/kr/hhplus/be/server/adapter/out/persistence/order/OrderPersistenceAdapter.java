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
        var toBeSavedOrder = OrderMapper.toJpa(order);
        var toBeSavedOrderItems = OrderItemMapper.toJpaMap(order.getOrderItems());

        var savedOrder = orderJpaRepository.save(toBeSavedOrder);
        var savedOrderItems = orderItemJpaRepository.saveAll(toBeSavedOrderItems);

        return OrderMapper.toDomain(savedOrder, savedOrderItems);
    }

    @Override
    public Order updateOrder(Order order) {
        var toBeUpdatedOrder = OrderMapper.toJpa(order);
        var toBeUpdatedOrderItems = OrderItemMapper.toJpaMap(order.getOrderItems());

        var UpdatedOrder = orderJpaRepository.save(toBeUpdatedOrder);
        var UpdatedOrderItems = orderItemJpaRepository.saveAll(toBeUpdatedOrderItems);

        return OrderMapper.toDomain(UpdatedOrder, UpdatedOrderItems);
    }
}
