package kr.hhplus.be.server.application.service;

import kr.hhplus.be.server.application.port.in.ListProductCommand;
import kr.hhplus.be.server.domain.model.Product;
import kr.hhplus.be.server.application.support.ApplicationUnitTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

class ListProductServiceTest extends ApplicationUnitTestSupport {

    @InjectMocks ListProductService listProductService;
    @Mock Page<Product> products;

    final Pageable pageable = PageRequest.of(0, 10);
    final ListProductCommand command = new ListProductCommand(pageable);

    @Test
    @DisplayName("상품 목록 조회에 성공한다")
    void listProductSuccess() {
        // given
        // 상품 목록 조회
        doReturn(products).when(listProductPort).listProducts(pageable);

        // when
        Page<Product> result = listProductService.listProduct(command);

        // then
        assertThat(result).isEqualTo(products);

        // 상품 목록 조회 검증
        verify(listProductPort).listProducts(pageable);
    }
}