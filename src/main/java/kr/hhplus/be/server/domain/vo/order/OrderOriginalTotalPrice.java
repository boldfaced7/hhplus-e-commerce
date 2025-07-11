package kr.hhplus.be.server.domain.vo.order;

import kr.hhplus.be.server.domain.vo.coupon.CouponDiscountRate;

public record OrderOriginalTotalPrice(Long value) {

    public OrderDiscountTotalPrice discount(CouponDiscountRate discountRate) {
        return new OrderDiscountTotalPrice(Math.round(value * (1.0 - discountRate.value())));
    }

}
