package kr.hhplus.be.server.adapter.out.persistence.coupon;

import kr.hhplus.be.server.application.port.out.coupon.LoadCouponPort;
import kr.hhplus.be.server.application.port.out.coupon.SaveCouponPort;
import kr.hhplus.be.server.application.port.out.coupon.UpdateCouponPort;
import kr.hhplus.be.server.domain.model.Coupon;
import kr.hhplus.be.server.domain.vo.coupon.CouponId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CouponPersistenceAdapter implements 
        LoadCouponPort,
        SaveCouponPort,
        UpdateCouponPort
{

    private final CouponJpaRepository couponJpaRepository;

    @Override
    public Optional<Coupon> loadCoupon(CouponId couponId) {
        return couponJpaRepository.findById(couponId.value())
                .map(CouponMapper::toDomain);
    }

    @Override
    public Coupon saveCoupon(Coupon coupon) {
        var saved = couponJpaRepository.save(CouponMapper.toJpa(coupon));
        return CouponMapper.toDomain(saved);
    }

    @Override
    public Coupon updateCoupon(Coupon coupon) {
        var updated = couponJpaRepository.save(CouponMapper.toJpa(coupon));
        return CouponMapper.toDomain(updated);
    }

}
