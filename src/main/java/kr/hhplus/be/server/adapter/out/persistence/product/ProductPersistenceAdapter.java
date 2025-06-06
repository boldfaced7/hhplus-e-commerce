package kr.hhplus.be.server.adapter.out.persistence.product;

import kr.hhplus.be.server.application.port.out.product.ListProductPort;
import kr.hhplus.be.server.application.port.out.product.LoadProductPort;
import kr.hhplus.be.server.application.port.out.product.SaveProductPort;
import kr.hhplus.be.server.application.port.out.product.UpdateProductPort;
import kr.hhplus.be.server.domain.model.Product;
import kr.hhplus.be.server.domain.vo.product.ProductId;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ProductPersistenceAdapter implements
        ListProductPort,
        LoadProductPort,
        SaveProductPort,
        UpdateProductPort
{

    private final ProductJpaRepository productJpaRepository;

    @Override
    public Page<Product> listProducts(Pageable pageable) {
        return productJpaRepository.findAll(pageable).map(ProductMapper::toDomain);
    }

    @Override
    public Map<ProductId, Product> listProducts(Collection<ProductId> productIds) {
        var ids = productIds.stream().map(ProductId::value).toList();
        var listed = productJpaRepository.listProducts(ids);
        return ProductMapper.toDomainMap(listed);
    }

    @Override
    public Optional<Product> loadProduct(ProductId productId) {
        return productJpaRepository.findById(productId.value())
                .map(ProductMapper::toDomain);
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
    public Product updateProduct(Product product) {
        var updated = productJpaRepository.save(ProductMapper.toJpa(product));
        return ProductMapper.toDomain(updated);
    }

    @Override
    public Collection<Product> updateProducts(Collection<Product> products) {
        var updated = productJpaRepository.saveAll(ProductMapper.toJpa(products));
        return ProductMapper.toDomain(updated);
    }
}
