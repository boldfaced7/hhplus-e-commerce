package kr.hhplus.be.server.application.port.in;

import kr.hhplus.be.server.domain.vo.coupon.CouponId;
import kr.hhplus.be.server.domain.vo.orderitem.OrderItemQuantity;
import kr.hhplus.be.server.domain.vo.product.ProductId;
import kr.hhplus.be.server.domain.vo.user.UserId;

import java.util.Collection;
import java.util.Map;

public record PlaceOrderCommand(
        UserId userId,
        Map<ProductId, OrderItemQuantity> orderItemQuantities,
        CouponId couponId
) {

    public Collection<ProductId> productIds() {
        return orderItemQuantities.keySet();
    }
}
