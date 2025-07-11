package kr.hhplus.be.server.application.service;

import kr.hhplus.be.server.application.port.in.ListBestSellingProductCommand;
import kr.hhplus.be.server.application.port.out.product.ListBestSellingProductIdPort;
import kr.hhplus.be.server.application.port.out.product.LoadProductsPort;
import kr.hhplus.be.server.domain.model.Product;
import kr.hhplus.be.server.domain.model.Products;
import kr.hhplus.be.server.domain.vo.product.ProductId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static kr.hhplus.be.server.application.service.TestConstants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ListBestSellingProductServiceTest {

    @InjectMocks ListBestSellingProductService listBestSellingProductService;
    @Mock ListBestSellingProductIdPort listBestSellingProductIdPort;
    @Mock LoadProductsPort loadProductsPort;
    @Mock Products products;
    @Mock Product product;

    final Pageable pageable = PageRequest.of(0, 10);
    final ListBestSellingProductCommand command = new ListBestSellingProductCommand(pageable);
    final Page<ProductId> productIdPage = new PageImpl<>(PRODUCT_IDS, pageable, 1);

    @Test
    @DisplayName("인기 상품 목록 조회에 성공한다")
    void listBestSellingProductSuccess() {
        // given
        doReturn(productIdPage).when(listBestSellingProductIdPort).listBestSellingProductId(pageable);
        doReturn(products).when(loadProductsPort).loadProducts(PRODUCT_IDS);
        doReturn(product).when(products).getProduct(PRODUCT_ID);

        // when
        Page<Product> result = listBestSellingProductService.listBestSellingProductToady(command);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0)).isEqualTo(product);

        // 인기 상품 ID 조회 검증
        verify(listBestSellingProductIdPort).listBestSellingProductId(pageable);
        // 상품 정보 조회 검증
        verify(loadProductsPort).loadProducts(PRODUCT_IDS);
    }

    @Test
    @DisplayName("인기 상품이 없는 경우 빈 페이지를 반환한다")
    void listBestSellingProductEmpty() {
        // given
        Page<ProductId> emptyPage = new PageImpl<>(List.of(), pageable, 0);
        doReturn(emptyPage).when(listBestSellingProductIdPort).listBestSellingProductId(pageable);

        // when
        Page<Product> result = listBestSellingProductService.listBestSellingProductToady(command);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isZero();

        // 인기 상품 ID 조회 검증
        verify(listBestSellingProductIdPort).listBestSellingProductId(pageable);
    }
} 