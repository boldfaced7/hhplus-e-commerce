package kr.hhplus.be.server.application.service.saga;

import kr.hhplus.be.server.domain.vo.coupon.CouponId;
import kr.hhplus.be.server.domain.vo.order.OrderDiscountTotalPrice;
import kr.hhplus.be.server.domain.vo.order.OrderId;
import kr.hhplus.be.server.domain.vo.orderitem.OrderItemQuantities;
import kr.hhplus.be.server.domain.vo.user.UserId;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.UUID;

@ToString
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PlaceOrderSagaContext {
    private UUID uuid;
    private OrderId orderId;
    private UserId userId;
    private CouponId couponId;
    private OrderDiscountTotalPrice price;
    private OrderItemQuantities quantities;

    public static PlaceOrderSagaContext create(
            java.util.UUID uuid,
            OrderId orderId,
            UserId userId,
            CouponId couponId,
            OrderDiscountTotalPrice price,
            OrderItemQuantities quantities
    ) {
        return new PlaceOrderSagaContext(
                uuid,
                orderId,
                userId,
                couponId,
                price,
                quantities
        );
    }
}
