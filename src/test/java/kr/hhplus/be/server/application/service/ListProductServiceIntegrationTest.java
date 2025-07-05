package kr.hhplus.be.server.application.service;

import kr.hhplus.be.server.application.port.in.ListProductCommand;
import kr.hhplus.be.server.application.port.out.product.SaveProductPort;
import kr.hhplus.be.server.domain.model.Product;
import kr.hhplus.be.server.domain.vo.product.ProductName;
import kr.hhplus.be.server.domain.vo.product.ProductPrice;
import kr.hhplus.be.server.domain.vo.product.ProductStock;
import kr.hhplus.be.server.application.support.TestContainerSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;

import static org.assertj.core.api.Assertions.assertThat;

class ListProductServiceIntegrationTest extends TestContainerSupport {

    @Autowired ListProductService listProductService;
    @Autowired SaveProductPort saveProductPort;

    ListProductCommand command1;
    ListProductCommand command2;

    @Test
    @DisplayName("여러 상품이 있는 경우 페이지 단위로 조회된다.")
    void listProductsWithPagination() {
        // given
        setUpTestData();

        // when
        var page1 = listProductService.listProduct(command1);
        var page2 = listProductService.listProduct(command2);

        // then
        assertThat(page1.getTotalElements()).isEqualTo(11);
        assertThat(page1.getTotalPages()).isEqualTo(2);
        assertThat(page1.getContent()).hasSize(10);
        assertThat(page2.getContent()).hasSize(1);
    }

    void setUpTestData() {
        for (int i = 0; i < 11; i++) {
            saveProductPort.saveProduct(Product.create(
                    new ProductName("상품" + i),
                    new ProductPrice(i * 10000L),
                    new ProductStock(i * 10L)
            ));
        }

        Pageable pageable = Pageable.ofSize(10);
        command1 = new ListProductCommand(pageable);
        command2 = new ListProductCommand(pageable.next());

    }

}