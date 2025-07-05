package kr.hhplus.be.server.domain.event.product;

import kr.hhplus.be.server.domain.event.DomainEvent;
import kr.hhplus.be.server.domain.vo.coupon.CouponId;
import kr.hhplus.be.server.domain.vo.order.OrderDiscountTotalPrice;
import kr.hhplus.be.server.domain.vo.order.OrderId;
import kr.hhplus.be.server.domain.vo.orderitem.OrderItemQuantity;
import kr.hhplus.be.server.domain.vo.product.ProductId;
import kr.hhplus.be.server.domain.vo.user.UserId;

import java.util.Collection;
import java.util.Map;

public record StockDeducted(
        OrderId orderId,
        CouponId couponId,
        UserId userId,
        OrderDiscountTotalPrice orderDiscountTotalPrice,
        Map<ProductId, OrderItemQuantity> orderItemQuantities,
        Collection<ProductId> productIds
) implements DomainEvent {
    
    public StockDeducted(DomainEvent event) {
        this(
                event.orderId(),
                event.couponId(),
                event.userId(),
                event.orderDiscountTotalPrice(),
                event.orderItemQuantities(),
                event.productIds()
        );
    }
}
