package kr.hhplus.be.server.domain.vo.order;

import kr.hhplus.be.server.domain.vo.coupon.CouponDiscountRate;
import kr.hhplus.be.server.domain.vo.orderitem.OrderItemQuantity;
import kr.hhplus.be.server.domain.vo.product.ProductId;
import kr.hhplus.be.server.domain.vo.product.ProductPrice;

import java.util.Map;

public record OrderOriginalTotalPrice(Long value) {
    public OrderOriginalTotalPrice(
            Map<ProductId, OrderItemQuantity> orderItemQuantities,
            Map<ProductId, ProductPrice> productPrices
    ) {
        this(calculatePrice(orderItemQuantities, productPrices));
    }

    private static Long calculatePrice(
            Map<ProductId, OrderItemQuantity> orderItemQuantities,
            Map<ProductId, ProductPrice> productPrices
    ) {
        return productPrices.keySet().stream()
                .map(id -> orderItemQuantities.get(id).value() * productPrices.get(id).value())
                .reduce(0L, Long::sum);
    }

    public OrderDiscountTotalPrice discount(CouponDiscountRate discountRate) {
        return new OrderDiscountTotalPrice(Math.round(value * (1.0 - discountRate.value())));
    }
}
