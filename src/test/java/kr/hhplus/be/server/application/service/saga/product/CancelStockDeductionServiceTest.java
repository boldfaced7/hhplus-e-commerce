package kr.hhplus.be.server.application.service.saga.product;

import kr.hhplus.be.server.application.service.saga.product.impl.CancelStockDeductionServiceImpl;
import kr.hhplus.be.server.application.support.ApplicationUnitTestSupport;
import kr.hhplus.be.server.domain.model.Product;
import kr.hhplus.be.server.domain.model.Products;
import kr.hhplus.be.server.domain.vo.product.ProductId;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import java.util.HashMap;
import java.util.Map;

import static kr.hhplus.be.server.application.service.TestConstants.*;
import static kr.hhplus.be.server.application.service.saga.SagaTestUtils.setUpProducts;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class CancelStockDeductionServiceTest extends ApplicationUnitTestSupport {

    @InjectMocks CancelStockDeductionServiceImpl cancelStockDeductionService;

    @Test
    void 재고_차감_취소_성공() {
        // given
        Products products = spy(setUpProducts(PRODUCT_STOCK_DEDUCTED));
        stubCancelStockDeduction(products, true);
        
        // when
        cancelStockDeductionService.cancelStockDeduction(CANCEL_STOCK_DEDUCTION_COMMAND);
        
        // then
        verifyCancelStockDeduction(products, true);
    }

    @Test
    void 재고_차감_취소_실패() {
        // given
        Map<ProductId, Product> productMap = new HashMap<>();
        productMap.put(PRODUCT_ID, null);
        Products products = spy(Products.create(productMap));
        stubCancelStockDeduction(products, false);

        // when & then
        assertThatThrownBy(() -> {
            cancelStockDeductionService.cancelStockDeduction(CANCEL_STOCK_DEDUCTION_COMMAND);
        }).isInstanceOf(RuntimeException.class);
        verifyCancelStockDeduction(products, false);
    }

    void stubCancelStockDeduction(Products products, boolean isSucceeded) {
        // 재고 조회
        when(loadProductsPort.loadProductsForUpdate(anyCollection())).thenReturn(products);
        if (isSucceeded) {
            // 재고 차감 취소 업데이트
            when(updateProductsPort.updateProducts(products)).thenReturn(products);
            // 이벤트 발행
            doNothing().when(publishEventPort).publishEvents(any(Products.class));
        }
    }

    void verifyCancelStockDeduction(Products products, boolean succeeded) {
        // 재고 조회
        verify(loadProductsPort).loadProductsForUpdate(anyCollection());
        if (!products.hasAllProducts()) return;

        if (succeeded) {
            // 재고 차감 취소
            verify(products).restoreAll(UUID_RANDOM, ORDER_ITEM_QUANTITIES);
            // 재고 차감 취소 업데이트
            verify(updateProductsPort).updateProducts(products);
            // 이벤트 발행
            verify(publishEventPort).publishEvents(any(Products.class));
        }
    }
}