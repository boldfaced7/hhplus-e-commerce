package kr.hhplus.be.server.domain.model;

import kr.hhplus.be.server.domain.exception.CouponAlreadyUsedException;
import kr.hhplus.be.server.domain.vo.coupon.CouponDiscountRate;
import kr.hhplus.be.server.domain.vo.coupon.CouponId;
import kr.hhplus.be.server.domain.vo.coupon.CouponUsed;
import kr.hhplus.be.server.domain.vo.user.UserId;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Coupon {
    private CouponId couponId;
    private UserId userId;
    private CouponDiscountRate discountRate;
    private CouponUsed used;
    private LocalDateTime issuedAt;
    private LocalDateTime usedAt;

    public static Coupon create(
            UserId userId,
            CouponDiscountRate discountRate,
            CouponUsed used
    ) {
        return new Coupon(
                null,
                userId,
                discountRate,
                used,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    public static Coupon create(
            CouponId couponId,
            UserId userId,
            CouponDiscountRate discountRate,
            CouponUsed used,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        return new Coupon(
                couponId,
                userId,
                discountRate,
                used,
                createdAt,
                updatedAt
        );
    }

    public void use() {
        if (this.used.value()) {
            throw new CouponAlreadyUsedException();
        }
        this.used = CouponUsed.USED;
        this.usedAt = LocalDateTime.now();
    }

    public void cancel() {
        this.used = CouponUsed.NOT_USED;
        this.usedAt = issuedAt;
    }
}
