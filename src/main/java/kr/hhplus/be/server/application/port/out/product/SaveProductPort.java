package kr.hhplus.be.server.application.port.out.product;

import kr.hhplus.be.server.domain.model.Product;

import java.util.Collection;

public interface SaveProductPort {
    Product saveProduct(Product product);
    Collection<Product> saveProducts(Collection<Product> products);
}
