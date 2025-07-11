package kr.hhplus.be.server.application.service;

import kr.hhplus.be.server.application.port.in.ListBestSellingProductCommand;
import kr.hhplus.be.server.application.port.in.ListBestSellingProductQuery;
import kr.hhplus.be.server.application.port.out.product.ListBestSellingProductIdPort;
import kr.hhplus.be.server.application.port.out.product.LoadProductsPort;
import kr.hhplus.be.server.domain.model.Product;
import kr.hhplus.be.server.domain.model.Products;
import kr.hhplus.be.server.domain.vo.product.ProductId;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ListBestSellingProductService implements ListBestSellingProductQuery {

    private final ListBestSellingProductIdPort listBestSellingProductIdPort;
    private final LoadProductsPort loadProductsPort;

    @Override
    public Page<Product> listBestSellingProductToady(ListBestSellingProductCommand command) {
        Page<ProductId> productIds = listBestSellingProductIdPort
                .listBestSellingProductId(command.pageable());

        if (productIds.isEmpty()) {
            return Page.empty();
        }

        Products products = loadProductsPort.loadProducts(productIds.toList());
        return productIds.map(products::getProduct);
    }

}
