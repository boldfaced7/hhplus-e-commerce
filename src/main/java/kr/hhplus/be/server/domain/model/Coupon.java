package kr.hhplus.be.server.domain.model;

import kr.hhplus.be.server.domain.event.coupon.CouponUseCancelled;
import kr.hhplus.be.server.domain.event.coupon.CouponUseFailed;
import kr.hhplus.be.server.domain.exception.CouponAlreadyUsedException;
import kr.hhplus.be.server.domain.exception.CouponUnavailableException;
import kr.hhplus.be.server.domain.vo.coupon.CouponDiscountRate;
import kr.hhplus.be.server.domain.vo.coupon.CouponId;
import kr.hhplus.be.server.domain.vo.coupon.CouponUsed;
import kr.hhplus.be.server.domain.vo.user.UserId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.UUID;

@ToString
@Getter
@AllArgsConstructor
public class Coupon extends EventDomain {

    private CouponId couponId;
    private UserId userId;
    private CouponDiscountRate discountRate;
    private CouponUsed used;
    private LocalDateTime issuedAt;
    private LocalDateTime usedAt;

    public static Coupon unavailable() {
        return new Coupon(
            null,
            null,
            null,
            null,
            LocalDateTime.MAX,
            LocalDateTime.MAX
        );
    }

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

    public void use(UUID uuid) {
        checkCouponAvailable();

        if (this.used.equals(CouponUsed.USED)) {
            markCouponUseFailed(uuid);
            throw new CouponAlreadyUsedException(couponId);
        }
        this.used = CouponUsed.USED;
        this.usedAt = LocalDateTime.now();
        markCouponUsed(uuid);
    }

    public void restore(UUID uuid) {
        checkCouponAvailable();

        this.used = CouponUsed.NOT_USED;
        this.usedAt = issuedAt;
        markCouponUseCancelled(uuid);
    }

    public boolean isCouponUnavailable() {
        return couponId == null && userId == null
                && discountRate == null && used == null
                && issuedAt == null && usedAt == null;
    }

    public void checkCouponAvailable() {
        if (isCouponUnavailable()) {
            throw new CouponUnavailableException();
        }
    }

    private void markCouponUsed(UUID uuid) {
        addEvent(new kr.hhplus.be.server.domain.event.coupon.CouponUsed(uuid, couponId));
    }

    private void markCouponUseFailed(UUID uuid) {
        addEvent(new CouponUseFailed(uuid, couponId));
    }

    private void markCouponUseCancelled(UUID uuid) {
        addEvent(new CouponUseCancelled(uuid, couponId));
    }


}
