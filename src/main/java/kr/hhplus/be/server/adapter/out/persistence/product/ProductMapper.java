package kr.hhplus.be.server.adapter.out.persistence.product;

import kr.hhplus.be.server.domain.model.Product;
import kr.hhplus.be.server.domain.vo.product.ProductId;
import kr.hhplus.be.server.domain.vo.product.ProductName;
import kr.hhplus.be.server.domain.vo.product.ProductPrice;
import kr.hhplus.be.server.domain.vo.product.ProductStock;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

public class ProductMapper {

    public static ProductJpa toJpa(Product product) {
        return new ProductJpa(
            (product.getProductId() != null) ? product.getProductId().value() : null,
            product.getName().value(),
            product.getPrice().value(),
            product.getStock().value(),
            product.getCreatedAt(),
            product.getUpdatedAt()
        );
    }

    public static Product toDomain(ProductJpa productJpa) {
        return Product.create(
            new ProductId(productJpa.getId()),
            new ProductName(productJpa.getName()),
            new ProductPrice(productJpa.getPrice()),
            new ProductStock(productJpa.getStock()),
            productJpa.getCreatedAt(),
            productJpa.getUpdatedAt()
        );
    }


    public static Collection<ProductJpa> toJpa(Collection<Product> products) {
        return products.stream()
                .map(ProductMapper::toJpa)
                .toList();
    }


    public static Collection<Product> toDomain(Collection<ProductJpa> products) {
        return products.stream()
                .map(ProductMapper::toDomain)
                .toList();
    }

    public static Map<Long, ProductJpa> toJpaMap(Map<ProductId, Product> products) {
        return products.entrySet().stream()
                .collect(Collectors.toMap(
                        e -> e.getKey().value(),
                        e -> toJpa(e.getValue())
                ));
    }

    public static Map<ProductId, Product> toDomainMap(Map<Long, ProductJpa> products) {
        return products.entrySet().stream()
                .collect(Collectors.toMap(
                        e -> new ProductId(e.getKey()),
                        e -> toDomain(e.getValue())
                ));
    }

}
