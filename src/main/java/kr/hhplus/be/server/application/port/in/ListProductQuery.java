package kr.hhplus.be.server.application.port.in;

import kr.hhplus.be.server.domain.model.Product;
import org.springframework.data.domain.Page;

public interface ListProductQuery {
    Page<Product> listProduct(ListProductCommand command);
}
