package kr.hhplus.be.server.domain.model;

import kr.hhplus.be.server.domain.vo.coupon.CouponDiscountRate;
import kr.hhplus.be.server.domain.vo.orderitem.OrderItemQuantities;
import kr.hhplus.be.server.domain.vo.product.ProductId;
import kr.hhplus.be.server.domain.vo.product.ProductPrices;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ToString
@Getter
@AllArgsConstructor
public class OrderItems {

    private Map<ProductId, OrderItem> orderItemMap;


    public static OrderItems create(
            OrderItemQuantities orderItemQuantities,
            ProductPrices productPrices,
            CouponDiscountRate discountRate
    ) {
        Map<ProductId, OrderItem> collected = orderItemQuantities.stream()
                .map(e -> OrderItem.create(
                        e.getKey(),
                        e.getValue(),
                        productPrices.getPrice(e.getKey()),
                        discountRate
                ))
                .collect(Collectors.toMap(
                        OrderItem::getProductId,
                        Function.identity()
                ));
        return new OrderItems(collected);
    }

    public OrderItemQuantities quantities() {
        var map = orderItemMap.values().stream()
                .collect(Collectors.toMap(
                        OrderItem::getProductId,
                        OrderItem::getQuantity
                ));
        return new OrderItemQuantities(map);
    }

    public Collection<ProductId> productIds() {
        return orderItemMap.keySet();
    }

    public Stream<Map.Entry<ProductId, OrderItem>> stream() {
        return orderItemMap.entrySet().stream();
    }

}
