package kr.hhplus.be.server.domain.vo.order;

import kr.hhplus.be.server.domain.vo.coupon.CouponDiscountRate;
import kr.hhplus.be.server.domain.vo.product.ProductPrice;

import java.util.Collection;

public record OrderOriginalTotalPrice(Long value) {
    public OrderOriginalTotalPrice(Collection<ProductPrice> productPrices) {
        this(productPrices.stream().map(ProductPrice::value).reduce(0L, Long::sum));
    }

    public OrderDiscountTotalPrice discount(CouponDiscountRate discountRate) {
        return new OrderDiscountTotalPrice(Math.round(value - value * discountRate.value()));
    }
}
