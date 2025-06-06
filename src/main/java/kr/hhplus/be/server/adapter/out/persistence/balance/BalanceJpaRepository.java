package kr.hhplus.be.server.adapter.out.persistence.balance;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BalanceJpaRepository extends JpaRepository<BalanceJpa, Long> {

    Optional<BalanceJpa> findByUserId(Long userId);
}
