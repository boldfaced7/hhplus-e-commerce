package kr.hhplus.be.server.application.port.out.product;

import kr.hhplus.be.server.domain.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ListProductPort {
    Page<Product> listProducts(Pageable pageable);
}
