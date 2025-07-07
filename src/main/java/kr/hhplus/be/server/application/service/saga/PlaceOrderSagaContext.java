package kr.hhplus.be.server.application.service.saga;

import kr.hhplus.be.server.domain.vo.coupon.CouponId;
import kr.hhplus.be.server.domain.vo.order.Guid;
import kr.hhplus.be.server.domain.vo.order.OrderDiscountTotalPrice;
import kr.hhplus.be.server.domain.vo.order.OrderId;
import kr.hhplus.be.server.domain.vo.orderitem.OrderItemQuantities;
import kr.hhplus.be.server.domain.vo.user.UserId;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PlaceOrderSagaContext {
    private Guid guid;
    private OrderId orderId;
    private UserId userId;
    private CouponId couponId;
    private OrderDiscountTotalPrice price;
    private OrderItemQuantities quantities;

    public static PlaceOrderSagaContext create(
            Guid guid,
            OrderId orderId,
            UserId userId,
            CouponId couponId,
            OrderDiscountTotalPrice price,
            OrderItemQuantities quantities
    ) {
        return new PlaceOrderSagaContext(
                guid,
                orderId,
                userId,
                couponId,
                price,
                quantities
        );
    }
}
