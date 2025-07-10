package kr.hhplus.be.server.domain.model;

import kr.hhplus.be.server.domain.vo.coupon.CouponDiscountRate;
import kr.hhplus.be.server.domain.vo.order.OrderId;
import kr.hhplus.be.server.domain.vo.orderitem.OrderItemDiscountPrice;
import kr.hhplus.be.server.domain.vo.orderitem.OrderItemId;
import kr.hhplus.be.server.domain.vo.orderitem.OrderItemOriginalPrice;
import kr.hhplus.be.server.domain.vo.orderitem.OrderItemQuantity;
import kr.hhplus.be.server.domain.vo.product.ProductId;
import kr.hhplus.be.server.domain.vo.product.ProductPrice;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@ToString
@Getter
@AllArgsConstructor
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

}
