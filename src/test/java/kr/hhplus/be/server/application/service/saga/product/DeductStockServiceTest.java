package kr.hhplus.be.server.application.service.saga.product;

import kr.hhplus.be.server.application.service.saga.product.impl.DeductStockServiceImpl;
import kr.hhplus.be.server.domain.model.Products;
import kr.hhplus.be.server.application.support.ApplicationUnitTestSupport;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;

import java.util.stream.Stream;

import static kr.hhplus.be.server.application.service.TestConstants.*;
import static kr.hhplus.be.server.application.service.saga.SagaTestUtils.setUpProducts;
import static org.mockito.Mockito.*;

class DeductStockServiceTest extends ApplicationUnitTestSupport {

    @InjectMocks DeductStockServiceImpl deductStockService;

    @MethodSource
    @ParameterizedTest(name = "[재고 차감] {0}")
    void 재고_차감(String message, Products products, boolean isSucceeded) {
        stubDeductStock(products, isSucceeded);   // given
        deductStockService.deductStock(EVENT);    // when
        verifyDeductStock(products, isSucceeded); // then
    }

    static Stream<Arguments> 재고_차감() {
        return Stream.of(
                Arguments.of("성공", spy(setUpProducts(PRODUCT_STOCK)), true),
                Arguments.of("실패", spy(setUpProducts(PRODUCT_STOCK_INSUFFICIENT)), false)
        );
    }

    void stubDeductStock(Products products, boolean isSucceeded) {
        // 재고 조회
        when(loadProductsPort.loadProductsForUpdate(PRODUCT_IDS)).thenReturn(products);
        if (isSucceeded) {
            // 재고 차감 업데이트
            when(updateProductsPort.updateProducts(products)).thenReturn(products);
        }
        // 이벤트 발행
        doNothing().when(publishEventPort).publishEvents(any(Products.class));
    }

    void verifyDeductStock(Products products, boolean isSucceeded) {
        int success = isSucceeded ? 1 : 0;
        int failure = isSucceeded ? 0 : 1;

        // 재고 조회
        verify(loadProductsPort).loadProductsForUpdate(PRODUCT_IDS);
        if (products != null) {
            // 재고 차감
            verify(products).deductAll(ORDER_ITEM_QUANTITIES);
            // 재고 차감 성공 이벤트 생성
            verify(products, times(success)).markStockDeducted(EVENT);
            // 재고 차감 실패 이벤트 생성
            verify(products, times(failure)).markStockDeductionFailed(EVENT);
        }
        // 재고 차감 업데이트
        verify(updateProductsPort, times(success)).updateProducts(products);
        // 이벤트 발행
        verify(publishEventPort).publishEvents(any(Products.class));
    }

}