package kr.hhplus.be.server.domain.model;

import kr.hhplus.be.server.domain.vo.coupon.CouponDiscountRate;
import kr.hhplus.be.server.domain.vo.coupon.CouponId;
import kr.hhplus.be.server.domain.vo.order.OrderDiscountTotalPrice;
import kr.hhplus.be.server.domain.vo.order.OrderId;
import kr.hhplus.be.server.domain.vo.order.OrderOriginalTotalPrice;
import kr.hhplus.be.server.domain.vo.orderitem.OrderItemQuantity;
import kr.hhplus.be.server.domain.vo.product.ProductId;
import kr.hhplus.be.server.domain.vo.product.ProductPrice;
import kr.hhplus.be.server.domain.vo.user.UserId;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Order {

    private OrderId orderId;
    private UserId userId;
    private CouponId couponId;
    private Map<ProductId, OrderItem> orderItems;
    private OrderOriginalTotalPrice originalTotalPrice;
    private OrderDiscountTotalPrice discountTotalPrice;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static Order create(
            UserId userId,
            CouponId couponId,
            Map<ProductId, OrderItemQuantity> orderItemQuantities,
            Map<ProductId, ProductPrice> productPrices,
            CouponDiscountRate discountRate
    ) {
        var original = new OrderOriginalTotalPrice(productPrices.values());

        return new Order(
                null,
                userId,
                couponId,
                OrderItem.create(orderItemQuantities, productPrices, discountRate),
                original,
                original.discount(discountRate),
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    public static Order create(
            OrderId orderId,
            UserId userId,
            CouponId couponId,
            Map<ProductId, OrderItem> orderItems,
            OrderOriginalTotalPrice originalTotalPrice,
            OrderDiscountTotalPrice discountTotalPrice,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        return new Order(
                orderId,
                userId,
                couponId,
                orderItems,
                originalTotalPrice,
                discountTotalPrice,
                createdAt,
                updatedAt
        );
    }

    public OrderItemQuantity getOrderItemQuantity(ProductId productId) {
        return  orderItems.get(productId).getQuantity();
    }
}
