package kr.hhplus.be.server.adapter.out.persistence.product;

import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public interface ProductJpaRepository extends JpaRepository<ProductJpa, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM ProductJpa p WHERE p.id IN :productIds")
    @QueryHints(
            @QueryHint(name = "jakarta.persistence.lock.timeout", value = "1000")
    )
    List<ProductJpa> findAllByIdForUpdate(Collection<Long> productIds);

    default Map<Long, ProductJpa> listProductsForUpdate(Collection<Long> productIds) {
        return listProducts(findAllByIdForUpdate(productIds), productIds);
    }

    default Map<Long, ProductJpa> listProducts(Collection<Long> productIds) {
        return listProducts(findAllById(productIds), productIds);
    }

    private Map<Long, ProductJpa> listProducts(
            List<ProductJpa> list,
            Collection<Long> productIds
    ) {
        var found = list.stream()
                .collect(Collectors.toMap(
                        ProductJpa::getId,
                        Function.identity()
                ));

        return productIds.stream().collect(Collectors.toMap(
                Function.identity(),
                found::get
        ));

    }
}
