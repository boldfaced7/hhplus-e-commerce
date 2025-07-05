package kr.hhplus.be.server.domain.vo.orderitem;

import kr.hhplus.be.server.domain.vo.coupon.CouponDiscountRate;
import kr.hhplus.be.server.domain.vo.product.ProductPrice;

public record OrderItemOriginalPrice(Long value) {

    public OrderItemOriginalPrice(ProductPrice productPrice) {
        this(productPrice.value());
    }

    public OrderItemDiscountPrice discount(CouponDiscountRate discountRate) {
        return new OrderItemDiscountPrice(Math.round(value - (1.0 * discountRate.value())));
    }
}
