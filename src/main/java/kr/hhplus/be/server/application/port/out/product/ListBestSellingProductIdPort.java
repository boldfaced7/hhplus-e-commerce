package kr.hhplus.be.server.application.port.out.product;

import kr.hhplus.be.server.domain.vo.product.ProductId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ListBestSellingProductIdPort {
    Page<ProductId> listBestSellingProductId(Pageable pageable);
}
