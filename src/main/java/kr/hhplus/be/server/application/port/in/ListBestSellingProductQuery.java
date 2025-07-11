package kr.hhplus.be.server.application.port.in;

import kr.hhplus.be.server.domain.model.Product;
import org.springframework.data.domain.Page;

public interface ListBestSellingProductQuery {
    Page<Product> listBestSellingProductToady(ListBestSellingProductCommand command);
}
