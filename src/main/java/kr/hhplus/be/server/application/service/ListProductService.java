package kr.hhplus.be.server.application.service;

import kr.hhplus.be.server.application.port.in.ListProductCommand;
import kr.hhplus.be.server.application.port.in.ListProductQuery;
import kr.hhplus.be.server.application.port.out.product.ListProductPort;
import kr.hhplus.be.server.domain.model.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ListProductService implements ListProductQuery {

    private final ListProductPort listProductPort;

    @Override
    public Page<Product> listProduct(ListProductCommand command) {
        return listProductPort.listProducts(command.pageable());
    }

}
