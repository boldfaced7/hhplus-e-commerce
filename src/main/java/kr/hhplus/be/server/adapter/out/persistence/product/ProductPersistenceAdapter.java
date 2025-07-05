package kr.hhplus.be.server.adapter.out.persistence.product;

import kr.hhplus.be.server.application.port.out.product.ListProductPort;
import kr.hhplus.be.server.application.port.out.product.LoadProductsPort;
import kr.hhplus.be.server.application.port.out.product.SaveProductPort;
import kr.hhplus.be.server.application.port.out.product.UpdateProductsPort;
import kr.hhplus.be.server.domain.model.Product;
import kr.hhplus.be.server.domain.model.Products;
import kr.hhplus.be.server.domain.vo.product.ProductId;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ProductPersistenceAdapter implements
        ListProductPort,
        LoadProductsPort,
        SaveProductPort,
        UpdateProductsPort
{

    private final ProductJpaRepository productJpaRepository;

    @Override
    public Page<Product> listProducts(Pageable pageable) {
        return productJpaRepository.findAll(pageable).map(ProductMapper::toDomain);
    }


    @Override
    public Products loadProductsForUpdate(Collection<ProductId> productIds) {
        return loadProducts(productIds, productJpaRepository::listProductsForUpdate);
    }

    @Override
    public Products loadProducts(Collection<ProductId> productIds) {
        return loadProducts(productIds, productJpaRepository::listProducts);
    }

    private Products loadProducts(
            Collection<ProductId> productIds,
            Function<Collection<Long>, Map<Long, ProductJpa>> loader
    ) {
        var ids = productIds.stream().map(ProductId::value).toList();
        var loadedJpa = loader.apply(ids);
        var loaded = ProductMapper.toDomainMap(loadedJpa);
        return Products.create(loaded);
    }

    @Override
    public Product saveProduct(Product product) {
        var saved = productJpaRepository.save(ProductMapper.toJpa(product));
        return ProductMapper.toDomain(saved);
    }

    @Override
    public Collection<Product> saveProducts(Collection<Product> products) {
        var saved = productJpaRepository.saveAll(ProductMapper.toJpa(products));
        return ProductMapper.toDomain(saved);
    }

    @Override
    public Products updateProducts(Products products) {
        var toBeUpdated = ProductMapper.toJpa(products.getProducts().values());

        var updated = productJpaRepository.saveAll(toBeUpdated).stream()
                .map(ProductMapper::toDomain)
                .collect(Collectors.toMap(
                        Product::getProductId,
                        Function.identity()
                ));
        return Products.create(updated);
    }
}
