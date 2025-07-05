package kr.hhplus.be.server.adapter.out.persistence.coupon;

import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;

import java.util.Optional;

public interface CouponJpaRepository extends JpaRepository<CouponJpa, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM CouponJpa c WHERE c.id = :id")
    @QueryHints(
            @QueryHint(name = "jakarta.persistence.lock.timeout", value = "1000")
    )
    Optional<CouponJpa> findByIdForUpdate(Long id);
}
