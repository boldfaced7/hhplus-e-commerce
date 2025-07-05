package kr.hhplus.be.server.domain.model;

import kr.hhplus.be.server.domain.event.order.OrderCreated;
import kr.hhplus.be.server.domain.event.order.OrderFailed;
import kr.hhplus.be.server.domain.event.order.OrderPlaced;
import kr.hhplus.be.server.domain.vo.order.Guid;
import kr.hhplus.be.server.domain.vo.coupon.CouponDiscountRate;
import kr.hhplus.be.server.domain.vo.coupon.CouponId;
import kr.hhplus.be.server.domain.vo.order.*;
import kr.hhplus.be.server.domain.vo.orderitem.OrderItemQuantity;
import kr.hhplus.be.server.domain.vo.product.ProductId;
import kr.hhplus.be.server.domain.vo.product.ProductPrice;
import kr.hhplus.be.server.domain.vo.user.UserId;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@ToString
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Order extends EventDomain {

    private OrderId orderId;
    private Guid guid;
    private UserId userId;
    private CouponId couponId;
    private Map<ProductId, OrderItem> orderItems;
    private OrderOriginalTotalPrice originalTotalPrice;
    private OrderDiscountTotalPrice discountTotalPrice;
    private OrderFinished orderFinished;
    private OrderSucceeded orderSucceeded;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static Order create(
            Guid guid,
            UserId userId,
            CouponId couponId,
            Map<ProductId, OrderItemQuantity> orderItemQuantities,
            Map<ProductId, ProductPrice> productPrices,
            CouponDiscountRate discountRate
    ) {
        var original = new OrderOriginalTotalPrice(
                orderItemQuantities,
                productPrices
        );
        return new Order(
                null,
                guid,
                userId,
                couponId,
                OrderItem.create(orderItemQuantities, productPrices, discountRate),
                original,
                original.discount(discountRate),
                new OrderFinished(false),
                new OrderSucceeded(false),
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    public static Order create(
            OrderId orderId,
            Guid guid,
            UserId userId,
            CouponId couponId,
            Map<ProductId, OrderItem> orderItems,
            OrderOriginalTotalPrice originalTotalPrice,
            OrderDiscountTotalPrice discountTotalPrice,
            OrderFinished orderFinished,
            OrderSucceeded orderSucceeded,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        return new Order(
                orderId,
                guid,
                userId,
                couponId,
                orderItems,
                originalTotalPrice,
                discountTotalPrice,
                orderFinished,
                orderSucceeded,
                createdAt,
                updatedAt
        );
    }


    public boolean isFinished() {
        return orderFinished.value();
    }

    public boolean isSucceeded() {
        return orderSucceeded.value();
    }

    public Map<ProductId, OrderItemQuantity> getOrderItemQuantities() {
        return orderItems.values().stream()
                .collect(Collectors.toMap(
                        OrderItem::getProductId,
                        OrderItem::getQuantity
                ));
    }

    public Collection<ProductId> getProductIds() {
        return orderItems.keySet();
    }

    public Order complete(OrderSucceeded isSucceeded) {
        this.orderFinished = new OrderFinished(true);
        this.orderSucceeded = isSucceeded;
        this.updatedAt = LocalDateTime.now();

        return this;
    }

    public void markOrderCreated() {
        addEvent(new OrderCreated(
                orderId,
                couponId,
                userId,
                getProductIds(),
                discountTotalPrice,
                getOrderItemQuantities()
        ));
    }

    public void markOrderFailed() {
        addEvent(new OrderFailed(
                orderId,
                couponId,
                userId,
                discountTotalPrice,
                getOrderItemQuantities(),
                getProductIds()
        ));

    }

    public void markOrderPlaced() {
        addEvent(new OrderPlaced(
                orderId,
                couponId,
                userId,
                discountTotalPrice,
                getOrderItemQuantities(),
                getProductIds()
        ));

    }

    public void apply(Consumer<Order> consumer) {
        consumer.accept(this);
    }
}
