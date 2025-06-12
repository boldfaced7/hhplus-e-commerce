package kr.hhplus.be.server.application.service;

import kr.hhplus.be.server.application.port.in.ListProductCommand;
import kr.hhplus.be.server.application.port.out.product.SaveProductPort;
import kr.hhplus.be.server.domain.model.Product;
import kr.hhplus.be.server.domain.vo.product.ProductName;
import kr.hhplus.be.server.domain.vo.product.ProductPrice;
import kr.hhplus.be.server.domain.vo.product.ProductStock;
import kr.hhplus.be.server.support.TestContainerSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;

import static org.assertj.core.api.Assertions.assertThat;

class ListProductServiceIntegrationTest extends TestContainerSupport {
    @Autowired
    private ListProductService listProductService;
    
    @Autowired
    private SaveProductPort saveProductPort;

    @Test
    @DisplayName("여러 상품이 있는 경우 페이지 단위로 조회된다.")
    void listProductsWithPagination() {
        // given
        var product1 = Product.create(
                new ProductName("상품1"),
                new ProductPrice(10000L),
                new ProductStock(100L)
        );
        product1 = saveProductPort.saveProduct(product1);

        var product2 = Product.create(
                new ProductName("상품2"),
                new ProductPrice(20000L),
                new ProductStock(200L)
        );
        product2 = saveProductPort.saveProduct(product2);

        var product3 = Product.create(
                new ProductName("상품3"),
                new ProductPrice(30000L),
                new ProductStock(300L)
        );
        product3 = saveProductPort.saveProduct(product3);

        // when
        var page1 = listProductService.listProduct(new ListProductCommand(Pageable.ofSize(2)));
        var page2 = listProductService.listProduct(new ListProductCommand(Pageable.ofSize(2).next()));

        // then
        assertThat(page1.getTotalElements()).isEqualTo(3);
        assertThat(page1.getTotalPages()).isEqualTo(2);
        assertThat(page1.getContent()).hasSize(2);
        assertThat(page2.getContent()).hasSize(1);
    }

} 