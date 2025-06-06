package kr.hhplus.be.server.adapter.out.persistence.orderitem;

import kr.hhplus.be.server.domain.model.OrderItem;
import kr.hhplus.be.server.domain.vo.order.OrderId;
import kr.hhplus.be.server.domain.vo.orderitem.OrderItemDiscountPrice;
import kr.hhplus.be.server.domain.vo.orderitem.OrderItemId;
import kr.hhplus.be.server.domain.vo.orderitem.OrderItemOriginalPrice;
import kr.hhplus.be.server.domain.vo.orderitem.OrderItemQuantity;
import kr.hhplus.be.server.domain.vo.product.ProductId;

import java.util.Map;
import java.util.stream.Collectors;

public class OrderItemMapper {

    public static OrderItemJpa toJpa(OrderItem orderItem) {
        return new OrderItemJpa(
            (orderItem.getOrderItemId() != null) ? orderItem.getOrderItemId().value() : null,
            orderItem.getOrderId().value(),
            orderItem.getProductId().value(),
            orderItem.getQuantity().value(),
            orderItem.getOriginalPrice().value(),
            orderItem.getDiscountPrice().value(),
            orderItem.getCreatedAt(),
            orderItem.getUpdatedAt()
        );
    }

    public static OrderItem toDomain(OrderItemJpa orderItemJpa) {
        return OrderItem.create(
            new OrderItemId(orderItemJpa.getId()),
            new OrderId(orderItemJpa.getOrderId()),
            new ProductId(orderItemJpa.getProductId()),
            new OrderItemQuantity(orderItemJpa.getQuantity()),
            new OrderItemOriginalPrice(orderItemJpa.getOriginalPrice()),
            new OrderItemDiscountPrice(orderItemJpa.getDiscountPrice()),
            orderItemJpa.getCreatedAt(),
            orderItemJpa.getUpdatedAt()
        );
    }

    public static Map<Long, OrderItemJpa> toJpaMap(Map<ProductId, OrderItem> orderItems) {
        return orderItems.entrySet().stream()
            .collect(Collectors.toMap(
                e -> e.getKey().value(),
                e -> toJpa(e.getValue())
            ));
    }

    public static Map<ProductId, OrderItem> toDomainMap(Map<Long, OrderItemJpa> orderItems) {
        return orderItems.entrySet().stream()
                .collect(Collectors.toMap(
                        e -> new ProductId(e.getKey()),
                        e -> toDomain(e.getValue())
                ));
    }

}
