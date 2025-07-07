package kr.hhplus.be.server.adapter.out.cache.context;

import kr.hhplus.be.server.application.service.saga.PlaceOrderSagaContext;
import kr.hhplus.be.server.domain.vo.coupon.CouponId;
import kr.hhplus.be.server.domain.vo.order.Guid;
import kr.hhplus.be.server.domain.vo.order.OrderDiscountTotalPrice;
import kr.hhplus.be.server.domain.vo.order.OrderId;
import kr.hhplus.be.server.domain.vo.orderitem.OrderItemQuantities;
import kr.hhplus.be.server.domain.vo.orderitem.OrderItemQuantity;
import kr.hhplus.be.server.domain.vo.product.ProductId;
import kr.hhplus.be.server.domain.vo.user.UserId;

import java.util.stream.Collectors;

public class RedisPlaceOrderSagaContextMapper {
    public static RedisPlaceOrderSagaContext toRedis(PlaceOrderSagaContext context) {
        return new RedisPlaceOrderSagaContext(
                context.getGuid().value(),
                context.getOrderId().value(),
                context.getUserId().value(),
                context.getCouponId().value(),
                context.getPrice().value(),
                context.getQuantities().stream()
                        .collect(Collectors.toMap(
                                entry -> entry.getKey().value(),
                                entry -> entry.getValue().value()
                        ))
        );
    }

    public static PlaceOrderSagaContext toDomain(RedisPlaceOrderSagaContext context) {
        return PlaceOrderSagaContext.create(
                new Guid(context.getGuid()),
                new OrderId(context.getOrderId()),
                new UserId(context.getUserId()),
                new CouponId(context.getCouponId()),
                new OrderDiscountTotalPrice(context.getPrice()),
                new OrderItemQuantities(context.getQuantities().entrySet().stream()
                        .collect(Collectors.toMap(
                                entry -> new ProductId(entry.getKey()),
                                entry -> new OrderItemQuantity(entry.getValue())
                        )))
        );
    }
}
