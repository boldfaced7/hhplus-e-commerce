package kr.hhplus.be.server.application.port.out.product;

import kr.hhplus.be.server.domain.model.Products;
import kr.hhplus.be.server.domain.vo.product.ProductId;

import java.util.Collection;

public interface LoadProductsPort {
    Products loadProducts(Collection<ProductId> productIds);
    Products loadProductsForUpdate(Collection<ProductId> productIds);
}
