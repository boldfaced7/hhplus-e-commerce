package kr.hhplus.be.server.adapter.out.persistence.coupon;

import kr.hhplus.be.server.domain.model.Coupon;
import kr.hhplus.be.server.domain.vo.coupon.CouponDiscountRate;
import kr.hhplus.be.server.domain.vo.coupon.CouponId;
import kr.hhplus.be.server.domain.vo.coupon.CouponUsed;
import kr.hhplus.be.server.domain.vo.user.UserId;

public class CouponMapper {

    public static CouponJpa toJpa(Coupon coupon) {
        return new CouponJpa(
            (coupon.getCouponId() != null) ? coupon.getCouponId().value() : null,
            coupon.getUserId().value(),
            coupon.getDiscountRate().value(),
            coupon.getUsed().value(),
            coupon.getIssuedAt(),
            coupon.getUsedAt()
        );
    }

    public static Coupon toDomain(CouponJpa couponJpa) {
        return Coupon.create(
            new CouponId(couponJpa.getId()),
            new UserId(couponJpa.getUserId()),
            new CouponDiscountRate(couponJpa.getDiscountRate()),
            new CouponUsed(couponJpa.isUsed()),
            couponJpa.getIssuedAt(),
            couponJpa.getUsedAt()
        );
    }
}
