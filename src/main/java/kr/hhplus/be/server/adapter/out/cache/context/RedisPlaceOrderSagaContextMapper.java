package kr.hhplus.be.server.adapter.out.cache.context;

import kr.hhplus.be.server.application.service.saga.PlaceOrderSagaContext;
import kr.hhplus.be.server.domain.vo.coupon.CouponId;
import kr.hhplus.be.server.domain.vo.order.OrderDiscountTotalPrice;
import kr.hhplus.be.server.domain.vo.order.OrderId;
import kr.hhplus.be.server.domain.vo.orderitem.OrderItemQuantities;
import kr.hhplus.be.server.domain.vo.orderitem.OrderItemQuantity;
import kr.hhplus.be.server.domain.vo.product.ProductId;
import kr.hhplus.be.server.domain.vo.user.UserId;

import java.util.UUID;
import java.util.stream.Collectors;

public class RedisPlaceOrderSagaContextMapper {
    public static RedisPlaceOrderSagaContext toRedis(PlaceOrderSagaContext context) {
        return new RedisPlaceOrderSagaContext(
                context.getUuid().toString(),
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

    public static PlaceOrderSagaContext toDomain(RedisPlaceOrderSagaContext redis) {
        return PlaceOrderSagaContext.create(
                UUID.fromString(redis.getUuid()),
                new OrderId(redis.getOrderId()),
                new UserId(redis.getUserId()),
                new CouponId(redis.getCouponId()),
                new OrderDiscountTotalPrice(redis.getPrice()),
                new OrderItemQuantities(redis.getQuantities().entrySet().stream()
                        .collect(Collectors.toMap(
                                entry -> new ProductId(entry.getKey()),
                                entry -> new OrderItemQuantity(entry.getValue())
                        )))
        );
    }
}
