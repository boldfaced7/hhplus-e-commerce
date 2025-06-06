package kr.hhplus.be.server.adapter.out.persistence.orderitem;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public interface OrderItemJpaRepository extends JpaRepository<OrderItemJpa, Long> {
    List<OrderItemJpa> findByOrderId(Long orderId);

    default Map<Long, OrderItemJpa> saveAll(Map<Long, OrderItemJpa> orderItems) {
        var saved = saveAll(orderItems.values());
        return saved.stream()
                .collect(Collectors.toMap(
                        OrderItemJpa::getProductId,
                        Function.identity())
                );
    }

    default Map<Long, OrderItemJpa> loadOrderItems(Long orderId) {
        return findByOrderId(orderId)
                .stream()
                .collect(Collectors.toMap(
                        OrderItemJpa::getProductId,
                        Function.identity())
                );
    }

}
