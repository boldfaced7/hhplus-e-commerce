package kr.hhplus.be.server.application.service;

import kr.hhplus.be.server.application.port.in.ListProductCommand;
import kr.hhplus.be.server.application.port.out.product.ListProductPort;
import kr.hhplus.be.server.domain.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ListProductServiceTest {

    @InjectMocks
    private ListProductService listProductService;

    @Mock private ListProductPort listProductPort;
    @Mock Page<Product> products;

    private Pageable pageable;
    private ListProductCommand command;

    @BeforeEach
    void setUp() {
        pageable = PageRequest.of(0, 10);
        command = new ListProductCommand(pageable);
    }

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