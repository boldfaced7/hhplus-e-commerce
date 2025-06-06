package kr.hhplus.be.server.application.port.out.product;

import kr.hhplus.be.server.domain.model.Product;
import kr.hhplus.be.server.domain.vo.product.ProductId;

import java.util.Optional;

public interface LoadProductPort {
    Optional<Product> loadProduct(ProductId productId);
}
