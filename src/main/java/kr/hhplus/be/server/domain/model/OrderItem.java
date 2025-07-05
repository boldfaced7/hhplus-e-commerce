package kr.hhplus.be.server.domain.model;

import kr.hhplus.be.server.domain.vo.coupon.CouponDiscountRate;
import kr.hhplus.be.server.domain.vo.order.OrderId;
import kr.hhplus.be.server.domain.vo.orderitem.OrderItemDiscountPrice;
import kr.hhplus.be.server.domain.vo.orderitem.OrderItemId;
import kr.hhplus.be.server.domain.vo.orderitem.OrderItemOriginalPrice;
import kr.hhplus.be.server.domain.vo.orderitem.OrderItemQuantity;
import kr.hhplus.be.server.domain.vo.product.ProductId;
import kr.hhplus.be.server.domain.vo.product.ProductPrice;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@ToString
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderItem {
    private OrderItemId orderItemId;
    private OrderId orderId;
    private ProductId productId;
    private OrderItemQuantity quantity;
    private OrderItemOriginalPrice originalPrice;
    private OrderItemDiscountPrice discountPrice;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static OrderItem create(
            ProductId productId,
            OrderItemQuantity quantity,
            ProductPrice productPrice,
            CouponDiscountRate discountRate
    ) {
        OrderItemOriginalPrice originalPrice = new OrderItemOriginalPrice(productPrice);
        return new OrderItem(
                null,
                null,
                productId,
                quantity,
                originalPrice,
                originalPrice.discount(discountRate),
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    public static Map<ProductId, OrderItem> create(
            Map<ProductId, OrderItemQuantity> orderItemQuantities,
            Map<ProductId, ProductPrice> productPrices,
            CouponDiscountRate discountRate
    ) {
        return orderItemQuantities.entrySet().stream()
                .map(e -> OrderItem.create(
                        e.getKey(),
                        e.getValue(),
                        productPrices.get(e.getKey()),
                        discountRate
                ))
                .collect(Collectors.toMap(
                        OrderItem::getProductId,
                        Function.identity()
                ));
    }

    public static OrderItem create(
            OrderItemId orderItemId,
            OrderId orderId,
            ProductId productId,
            OrderItemQuantity quantity,
            OrderItemOriginalPrice originalPrice,
            OrderItemDiscountPrice discountPrice,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        return new OrderItem(
                orderItemId,
                orderId,
                productId,
                quantity,
                originalPrice,
                discountPrice,
                createdAt,
                updatedAt
        );
    }
}
