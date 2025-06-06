package kr.hhplus.be.server.application.port.out.product;

import kr.hhplus.be.server.domain.model.Product;
import kr.hhplus.be.server.domain.vo.product.ProductId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Collection;
import java.util.Map;

public interface ListProductPort {
    Page<Product> listProducts(Pageable pageable);
    Map<ProductId, Product> listProducts(Collection<ProductId> productIds);
}
