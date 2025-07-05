package kr.hhplus.be.server.adapter.out.persistence.order;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderJpaRepository extends JpaRepository<OrderJpa, Long> {
    Optional<OrderJpa> findByGuid(Long guid);
}
