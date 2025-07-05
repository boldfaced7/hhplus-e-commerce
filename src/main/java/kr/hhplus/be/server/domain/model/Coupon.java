package kr.hhplus.be.server.domain.model;

import kr.hhplus.be.server.domain.event.DomainEvent;
import kr.hhplus.be.server.domain.event.coupon.CouponUseCancelled;
import kr.hhplus.be.server.domain.event.coupon.CouponUseFailed;
import kr.hhplus.be.server.domain.exception.CouponAlreadyUsedException;
import kr.hhplus.be.server.domain.exception.CouponUnavailableException;
import kr.hhplus.be.server.domain.vo.coupon.CouponDiscountRate;
import kr.hhplus.be.server.domain.vo.coupon.CouponId;
import kr.hhplus.be.server.domain.vo.coupon.CouponUsed;
import kr.hhplus.be.server.domain.vo.user.UserId;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.function.Consumer;

@ToString
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
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

    public static Coupon create(
            CouponId couponId,
            UserId userId,
            CouponDiscountRate discountRate,
            CouponUsed used,
            LocalDateTime issuedAt,
            LocalDateTime usedAt
    ) {
        return new Coupon(
                couponId,
                userId,
                discountRate,
                used,
                issuedAt,
                usedAt
        );
    }

    public Coupon use() {
        checkCouponAvailable();

        if (this.used.equals(CouponUsed.USED)) {
            throw new CouponAlreadyUsedException(couponId);
        }
        this.used = CouponUsed.USED;
        this.usedAt = LocalDateTime.now();

        return this;
    }

    public Coupon restore() {
        checkCouponAvailable();

        this.used = CouponUsed.NOT_USED;
        this.usedAt = issuedAt;
        return this;
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

    public void apply(Consumer<Coupon> consumer) {
        consumer.accept(this);
    }

    public void markCouponUsed(DomainEvent event) {
        addEvent(new kr.hhplus.be.server.domain.event.coupon.CouponUsed(event));
    }

    public void markCouponUseFailed(DomainEvent event) {
        addEvent(new CouponUseFailed(event));
    }

    public void markCouponUseCancelled(DomainEvent event) {
        addEvent(new CouponUseCancelled(event));
    }


}
