package kr.hhplus.be.server.application.port.in;

import kr.hhplus.be.server.domain.vo.coupon.CouponId;
import kr.hhplus.be.server.domain.vo.orderitem.OrderItemQuantities;
import kr.hhplus.be.server.domain.vo.product.ProductId;
import kr.hhplus.be.server.domain.vo.user.UserId;

import java.util.Collection;
import java.util.UUID;

public record PlaceOrderCommand(
        UUID uuid,
        UserId userId,
        CouponId couponId,
        OrderItemQuantities orderItemQuantities
) {

    public Collection<ProductId> productIds() {
        return orderItemQuantities.productIds();
    }
}
