package kr.hhplus.be.server.adapter.out.persistence.balance;

import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;

import java.util.Optional;

public interface BalanceJpaRepository extends JpaRepository<BalanceJpa, Long> {

    Optional<BalanceJpa> findByUserId(Long userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT b FROM BalanceJpa b WHERE b.userId = :userId")
    @QueryHints(
            @QueryHint(name = "jakarta.persistence.lock.timeout", value = "1000")
    )
    Optional<BalanceJpa> findByUserIdForUpdate(Long userId);
}
