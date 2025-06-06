package kr.hhplus.be.server.adapter.out.persistence.coupon;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CouponJpaRepository extends JpaRepository<CouponJpa, Long> {

    @Override
    Optional<CouponJpa> findById(Long aLong);
}
