package kr.hhplus.be.server.application.port.out.product;

import kr.hhplus.be.server.domain.model.Product;

import java.util.Collection;

public interface UpdateProductPort {
    Product updateProduct(Product product);
    Collection<Product> updateProducts(Collection<Product> products);
}
