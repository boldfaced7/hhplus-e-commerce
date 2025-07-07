package kr.hhplus.be.server.application.service.saga.product;

import kr.hhplus.be.server.application.service.saga.product.impl.DeductStockServiceImpl;
import kr.hhplus.be.server.domain.model.Products;
import kr.hhplus.be.server.application.support.ApplicationUnitTestSupport;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import static kr.hhplus.be.server.application.service.TestConstants.*;
import static kr.hhplus.be.server.application.service.saga.SagaTestUtils.setUpProducts;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DeductStockServiceTest extends ApplicationUnitTestSupport {

    @InjectMocks DeductStockServiceImpl deductStockService;

    @Test
    void 재고_차감_성공() {
        // given
        Products products = spy(setUpProducts(PRODUCT_STOCK));
        stubDeductStock(products, true);
        
        // when
        deductStockService.deductStock(DEDUCT_STOCK_COMMAND);
        
        // then
        verifyDeductStock(products, true);
    }

    @Test
    void 재고_차감_실패() {
        // given
        Products products = spy(setUpProducts(PRODUCT_STOCK_INSUFFICIENT));
        stubDeductStock(products, false);
        
        // when & then
        assertThatThrownBy(() -> {
            deductStockService.deductStock(DEDUCT_STOCK_COMMAND);
        }).isInstanceOf(RuntimeException.class);
        verifyDeductStock(products, false);
    }

    void stubDeductStock(Products products, boolean isSucceeded) {
        // 재고 조회
        when(loadProductsPort.loadProductsForUpdate(anyCollection())).thenReturn(products);
        if (isSucceeded) {
            // 재고 차감 업데이트
            when(updateProductsPort.updateProducts(products)).thenReturn(products);
        }
        // 이벤트 발행
        doNothing().when(publishEventPort).publishEvents(any(Products.class));
    }

    void verifyDeductStock(Products products, boolean isSucceeded) {
        // 재고 조회
        verify(loadProductsPort).loadProductsForUpdate(anyCollection());
        if (products == null) return;

        // 재고 차감
        verify(products).deductAll(GUID, ORDER_ITEM_QUANTITIES);

        if (isSucceeded) {
            // 재고 차감 업데이트
            verify(updateProductsPort).updateProducts(products);
        }
        // 이벤트 발행
        verify(publishEventPort).publishEvents(any(Products.class));
    }

}