package kr.hhplus.be.server.application.service.saga.product;

import kr.hhplus.be.server.application.service.saga.product.impl.CancelStockDeductionServiceImpl;
import kr.hhplus.be.server.domain.model.Product;
import kr.hhplus.be.server.domain.model.Products;
import kr.hhplus.be.server.domain.vo.product.ProductId;
import kr.hhplus.be.server.application.support.ApplicationUnitTestSupport;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;

import java.util.Map;
import java.util.stream.Stream;

import static kr.hhplus.be.server.application.service.TestConstants.*;
import static kr.hhplus.be.server.application.service.saga.SagaTestUtils.setUpProducts;
import static org.mockito.Mockito.*;

class CancelStockDeductionServiceTest extends ApplicationUnitTestSupport {

    @InjectMocks CancelStockDeductionServiceImpl cancelStockDeductionService;

    @MethodSource
    @ParameterizedTest(name = "[재고 차감 취소] {0}")
    void 재고_차감_취소(String message, Products products, boolean isSucceeded) {
        stubCancelStockDeduction(products, isSucceeded);         // given
        cancelStockDeductionService.cancelStockDeduction(EVENT); // when
        verifyCancelStockDeduction(products, isSucceeded);       // then
    }

    static Stream<Arguments> 재고_차감_취소() {
        Map<ProductId, Product> products = new java.util.HashMap<>();
        products.put(PRODUCT_ID, null);
        return Stream.of(
                Arguments.of("성공", spy(setUpProducts(PRODUCT_STOCK_DEDUCTED)), true),
                Arguments.of("실패", spy(Products.create(products)), false)
        );
    }

    void stubCancelStockDeduction(Products products, boolean isSucceeded) {
        // 재고 조회
        when(loadProductsPort.loadProductsForUpdate(PRODUCT_IDS)).thenReturn(products);
        if (isSucceeded) {
            // 재고 차감 취소 업데이트
            when(updateProductsPort.updateProducts(products)).thenReturn(products);
            // 이벤트 발행
            doNothing().when(publishEventPort).publishEvents(any(Products.class));
        }
    }

    void verifyCancelStockDeduction(Products products, boolean isSucceeded) {
        int times = isSucceeded ? 1 : 0;

        // 재고 조회
        verify(loadProductsPort).loadProductsForUpdate(PRODUCT_IDS);
        if (products.hasAllProducts()) {
            // 재고 차감 취소
            verify(products).addAll(ORDER_ITEM_QUANTITIES);
            // 이벤트 생성
            verify(products).markStockDeductionCancelled(EVENT);
        }
        // 재고 차감 취소 업데이트
        verify(updateProductsPort, times(times)).updateProducts(products);
        // 이벤트 발행
        verify(publishEventPort, times(times)).publishEvents(products);
    }
}