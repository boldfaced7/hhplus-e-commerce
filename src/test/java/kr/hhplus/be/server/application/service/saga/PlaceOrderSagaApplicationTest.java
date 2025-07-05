package kr.hhplus.be.server.application.service.saga;

import kr.hhplus.be.server.application.port.in.PlaceOrderCommand;
import kr.hhplus.be.server.application.port.in.PlaceOrderUseCase;
import kr.hhplus.be.server.application.port.out.balance.LoadBalancePort;
import kr.hhplus.be.server.application.port.out.balance.UpdateBalancePort;
import kr.hhplus.be.server.application.port.out.coupon.LoadCouponPort;
import kr.hhplus.be.server.application.port.out.coupon.UpdateCouponPort;
import kr.hhplus.be.server.application.port.out.order.LoadOrderPort;
import kr.hhplus.be.server.application.port.out.order.SaveOrderPort;
import kr.hhplus.be.server.application.port.out.order.UpdateOrderPort;
import kr.hhplus.be.server.application.port.out.product.ListProductPort;
import kr.hhplus.be.server.application.port.out.product.LoadProductsPort;
import kr.hhplus.be.server.application.port.out.product.UpdateProductsPort;
import kr.hhplus.be.server.domain.model.*;
import kr.hhplus.be.server.domain.vo.balance.BalanceAmount;
import kr.hhplus.be.server.domain.vo.coupon.CouponUsed;
import kr.hhplus.be.server.domain.vo.order.OrderId;
import kr.hhplus.be.server.domain.vo.product.ProductStock;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import static kr.hhplus.be.server.application.service.TestConstants.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Slf4j
@SpringBootTest
@ActiveProfiles("application-test")
class PlaceOrderSagaApplicationTest {

    @Autowired PlaceOrderUseCase placeOrderUseCase;

    @MockitoBean LoadCouponPort loadCouponPort;
    @MockitoBean UpdateCouponPort updateCouponPort;

    @MockitoBean LoadBalancePort loadBalancePort;
    @MockitoBean UpdateBalancePort updateBalancePort;

    @MockitoBean LoadProductsPort loadProductsPort;
    @MockitoBean ListProductPort listProductPort;
    @MockitoBean UpdateProductsPort updateProductsPort;

    @MockitoBean SaveOrderPort saveOrderPort;
    @MockitoBean LoadOrderPort loadOrderPort;
    @MockitoBean UpdateOrderPort updateOrderPort;

    final PlaceOrderCommand command = new PlaceOrderCommand(
            GUID,
            USER_ID,
            COUPON_ID,
            Map.of(PRODUCT_ID, QUANTITY)
    );

    final Order order = Order.create(
            ORDER_ID,
            GUID,
            USER_ID,
            COUPON_ID,
            Map.of(PRODUCT_ID, OrderItem.create(
                    PRODUCT_ID,
                    QUANTITY,
                    PRODUCT_PRICE,
                    COUPON_DISCOUNT_RATE
            )),
            ORDER_ORIGINAL_PRICE,
            ORDER_DISCOUNT_PRICE,
            ORDER_NOT_FINISHED,
            ORDER_FAILED,
            LocalDateTime.now(),
            LocalDateTime.now()
    );

    Products products;
    Coupon coupon;
    Balance balance;

    @BeforeEach
    void setUp() {
        clearInvocations(
                loadCouponPort,   updateCouponPort,
                loadBalancePort,  updateBalancePort,
                loadProductsPort, updateProductsPort, listProductPort,
                loadOrderPort ,   updateOrderPort,    saveOrderPort
        );
    }
    
    @Test
    @DisplayName("(1) 주문 생성 → (2) 쿠폰 사용 → (3) 잔액 차감 → (4) 재고 차감 → (5) 주문 성공 완료")
    void 주문_성공() {
        // given
        setUpTestData(COUPON_NOT_USED, BALANCE_AMOUNT, PRODUCT_STOCK);

        stubOrderCreation();                 // (1) 주문 생성 stubbing
        stubCouponForUse(true);              // (2) 쿠폰 사용 성공 stubbing
        stubBalanceForDeduction(true);       // (3) 잔액 차감 성공 stubbing
        stubProductsForStockDeduction(true); // (4) 재고 차감 성공 stubbing
        stubOrderForCompletion();            // (5) 주문 성공 완료 stubbing

        // when
        Order result = placeOrderUseCase.placeOrder(command);
        
        // then
        SagaTestUtils.verifySaga(result, () -> {
            verifyOrderCreation();      // (1) 주문 생성 검증
            verifyCoupon(true, false);  // (2) 쿠폰 사용 성공 검증
            verifyBalance(true, false); // (3) 잔액 차감 성공 검증
            verifyProduct(true);        // (4) 재고 차감 성공 검증
            verifyOrderCompletion();    // (5) 주문 성공 완료 검증
        });
    }

    @Test
    @DisplayName("(1) 주문 생성 → (2) 쿠폰 사용 실패 → (3) 주문 실패 완료")
    void 쿠폰_사용_불가() {
        // given
        setUpTestData(COUPON_USED, BALANCE_AMOUNT, PRODUCT_STOCK);

        stubOrderCreation();      // (1) 주문 생성 stubbing
        stubCouponForUse(false);  // (2) 쿠폰 사용 실패 stubbing
        stubOrderForCompletion(); // (3) 주문 실패 완료 stubbing

        // when
        Order result = placeOrderUseCase.placeOrder(command);
        
        // then
        SagaTestUtils.verifySaga(result, () -> {
            verifyOrderCreation();      // (1) 주문 생성 검증
            verifyCoupon(false, false); // (2) 쿠폰 사용 실패 검증
            verifyOrderCompletion();    // (3) 주문 실패 완료 검증
        });
    }

    @Test
    @DisplayName("(1) 주문 생성 → (2) 쿠폰 사용 → (3) 잔액 부족 → (4) 쿠폰 사용 취소 → (5) 주문 실패 완료")
    void 잔액_부족() {
        // given
        setUpTestData(COUPON_NOT_USED, BALANCE_AMOUNT_INSUFFICIENT, PRODUCT_STOCK);

        stubOrderCreation();            // (1) 주문 생성 stubbing
        stubCouponForUse(true);         // (2) 쿠폰 사용 성공, (4) 쿠폰 사용 취소 stubbing
        stubBalanceForDeduction(false); // (3) 잔액 부족 stubbing
        stubOrderForCompletion();       // (5) 주문 실패 완료 stubbing

        // when
        Order result = placeOrderUseCase.placeOrder(command);
        
        // then
        SagaTestUtils.verifySaga(result, () -> {
            verifyOrderCreation();       // (1) 주문 생성 검증
            verifyCoupon(true, true);    // (2) 쿠폰 사용 성공, (4) 쿠폰 사용 취소 검증
            verifyBalance(false, false); // (3) 잔액 부족 검증
            verifyOrderCompletion();     // (5) 주문 실패 완료 검증
        });
    }

    @Test
    @DisplayName("(1) 주문 생성 → (2) 쿠폰 사용 → (3) 잔액 차감 → (4) 재고 부족 → (5) 잔액 차감 취소 → (6) 쿠폰 사용 취소 → (7) 주문 실패 완료")
    void 재고_부족() {
        // given
        setUpTestData(COUPON_NOT_USED, BALANCE_AMOUNT, PRODUCT_STOCK_INSUFFICIENT);

        stubOrderCreation();                  // (1) 주문 생성 stubbing
        stubCouponForUse(true);               // (2) 쿠폰 사용, (6) 쿠폰 사용 취소 stubbing
        stubBalanceForDeduction(true);        // (3) 잔액 차감 성공, (5) 잔액 차감 취소 stubbing
        stubProductsForStockDeduction(false); // (4) 재고 부족 stubbing
        stubOrderForCompletion();             // (7) 주문 실패 완료 stubbing

        // when
        Order result = placeOrderUseCase.placeOrder(command);
        
        // then
        SagaTestUtils.verifySaga(result, () -> {
            verifyOrderCreation();     // (1) 주문 생성 검증
            verifyCoupon(true, true);  // (2) 쿠폰 사용 성공, (6) 쿠폰 사용 취소 검증
            verifyBalance(true, true); // (3) 잔액 차감 성공, (5) 잔액 차감 취소 검증
            verifyProduct(false);      // (4) 재고 부족 검증
            verifyOrderCompletion();   // (7) 주문 실패 완료 검증
        });
    }

    void stubOrderCreation() {
        when(loadProductsPort.loadProducts(anyCollection())).thenReturn(products);
        when(loadCouponPort.loadCoupon(COUPON_ID)).thenReturn(Optional.of(coupon));
        when(saveOrderPort.saveOrder(any(Order.class))).thenReturn(order);
    }

    void stubCouponForUse(boolean isUsed) {
        when(loadCouponPort.loadCouponForUpdate(COUPON_ID)).thenReturn(Optional.of(coupon));
        if (isUsed) {
            when(updateCouponPort.updateCoupon(any(Coupon.class))).thenReturn(coupon);
        }
    }

    void stubBalanceForDeduction(boolean isDeducted) {
        when(loadBalancePort.loadBalanceForUpdate(USER_ID)).thenReturn(Optional.of(balance));
        if (isDeducted) {
            when(updateBalancePort.updateBalance(any(Balance.class))).thenReturn(balance);
        }
    }

    void stubProductsForStockDeduction(boolean isDeducted) {
        when(loadProductsPort.loadProductsForUpdate(anyCollection())).thenReturn(products);
        if (isDeducted) {
            when(updateProductsPort.updateProducts(any(Products.class))).thenReturn(products);
        }
    }

    void stubOrderForCompletion() {
        when(loadOrderPort.loadOrder(any(OrderId.class))).thenReturn(Optional.of(order));
        when(updateOrderPort.updateOrder(any(Order.class))).thenReturn(order);
    }

    void verifyOrderCreation() {
        verify(loadProductsPort).loadProducts(anyCollection());
        verify(loadCouponPort).loadCoupon(COUPON_ID);
        verify(saveOrderPort).saveOrder(any(Order.class));
    }

    void verifyCoupon(boolean isUsed, boolean isCancelled) {
        int load = isCancelled ? 2 : 1;
        int update = isUsed && isCancelled ? 2 : isUsed ? 1 : 0;

        verify(loadCouponPort, times(load)).loadCouponForUpdate(COUPON_ID);
        verify(updateCouponPort, times(update)).updateCoupon(any(Coupon.class));
    }

    void verifyBalance(boolean isDeducted, boolean isCancelled) {
        int load = isCancelled ? 2 : 1;
        int update = isDeducted && isCancelled ? 2 : isDeducted ? 1 : 0;

        verify(loadBalancePort, times(load)).loadBalanceForUpdate(USER_ID);
        verify(updateBalancePort, times(update)).updateBalance(any(Balance.class));
    }

    void verifyProduct(boolean isDeducted) {
        int load = 1;
        int update = isDeducted ? 1 : 0;

        verify(loadProductsPort, times(load)).loadProductsForUpdate(anyCollection());
        verify(updateProductsPort, times(update)).updateProducts(any(Products.class));
    }

    void verifyOrderCompletion() {
        verify(loadOrderPort).loadOrder(any(OrderId.class));
        verify(updateOrderPort).updateOrder(any(Order.class));
    }


    void setUpTestData(CouponUsed couponUsed, BalanceAmount balanceAmount, ProductStock productStock) {
        coupon   = SagaTestUtils.setUpCoupon(couponUsed);
        balance  = SagaTestUtils.setUpBalance(balanceAmount);
        products = SagaTestUtils.setUpProducts(productStock);
    }
    
}