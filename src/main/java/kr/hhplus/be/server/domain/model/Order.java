package kr.hhplus.be.server.domain.model;

import kr.hhplus.be.server.domain.event.order.OrderCreated;
import kr.hhplus.be.server.domain.event.order.OrderFailed;
import kr.hhplus.be.server.domain.event.order.OrderPlaced;
import kr.hhplus.be.server.domain.vo.coupon.CouponDiscountRate;
import kr.hhplus.be.server.domain.vo.coupon.CouponId;
import kr.hhplus.be.server.domain.vo.order.*;
import kr.hhplus.be.server.domain.vo.orderitem.OrderItemQuantities;
import kr.hhplus.be.server.domain.vo.product.ProductPrices;
import kr.hhplus.be.server.domain.vo.user.UserId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.UUID;

@ToString
@Getter
@AllArgsConstructor
public class Order extends EventDomain {

    private OrderId orderId;
    private UUID uuid;
    private UserId userId;
    private CouponId couponId;
    private OrderItems orderItems;
    private OrderOriginalTotalPrice originalTotalPrice;
    private OrderDiscountTotalPrice discountTotalPrice;
    private OrderFinished orderFinished;
    private OrderSucceeded orderSucceeded;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static Order create(
            UUID uuid,
            UserId userId,
            CouponId couponId,
            OrderItemQuantities orderItemQuantities,
            ProductPrices productPrices,
            CouponDiscountRate discountRate
    ) {
        var orderItems = OrderItems.create(
                orderItemQuantities,
                productPrices,
                discountRate
        );
        var originalPrice = productPrices.calculateOriginalPrice(
                orderItemQuantities
        );
        var discountPrice = originalPrice.discount(
                discountRate
        );
        var order = new Order(
                null,
                uuid,
                userId,
                couponId,
                orderItems,
                originalPrice,
                discountPrice,
                OrderFinished.NOT_FINISHED,
                OrderSucceeded.FAILED,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        order.markOrderCreated();
        return order;
    }

    public boolean isSucceeded() {
        return orderSucceeded.value();
    }

    public void complete(OrderSucceeded orderSucceeded) {
        this.orderFinished = OrderFinished.FINISHED;
        this.orderSucceeded = orderSucceeded;
        this.updatedAt = LocalDateTime.now();

        if (isSucceeded()) {
            markOrderPlaced();
        } else {
            markOrderFailed();
        }
    }

    public void markOrderCreated() {
        addEvent(new OrderCreated(
                uuid,
                orderId,
                userId,
                couponId,
                discountTotalPrice,
                orderItems.quantities()
        ));
    }

    private void markOrderFailed() {
        addEvent(new OrderFailed(uuid, orderId));
    }

    private void markOrderPlaced() {
        addEvent(new OrderPlaced(uuid, orderId));
    }

}
