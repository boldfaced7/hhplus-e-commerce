package kr.hhplus.be.server.application.service.saga;

import kr.hhplus.be.server.adapter.out.persistence.balance.BalanceJpaRepository;
import kr.hhplus.be.server.adapter.out.persistence.balance.BalancePersistenceAdapter;
import kr.hhplus.be.server.adapter.out.persistence.coupon.CouponJpaRepository;
import kr.hhplus.be.server.adapter.out.persistence.coupon.CouponPersistenceAdapter;
import kr.hhplus.be.server.adapter.out.persistence.order.OrderJpaRepository;
import kr.hhplus.be.server.adapter.out.persistence.order.OrderPersistenceAdapter;
import kr.hhplus.be.server.adapter.out.persistence.orderitem.OrderItemJpaRepository;
import kr.hhplus.be.server.adapter.out.persistence.product.ProductJpaRepository;
import kr.hhplus.be.server.adapter.out.persistence.product.ProductPersistenceAdapter;
import kr.hhplus.be.server.application.port.in.PlaceOrderCommand;
import kr.hhplus.be.server.application.port.in.PlaceOrderUseCase;
import kr.hhplus.be.server.domain.model.Balance;
import kr.hhplus.be.server.domain.model.Coupon;
import kr.hhplus.be.server.domain.model.Product;
import kr.hhplus.be.server.domain.model.Products;
import kr.hhplus.be.server.domain.vo.balance.BalanceAmount;
import kr.hhplus.be.server.domain.vo.coupon.CouponId;
import kr.hhplus.be.server.domain.vo.coupon.CouponUsed;
import kr.hhplus.be.server.domain.vo.order.OrderId;
import kr.hhplus.be.server.domain.vo.order.OrderSucceeded;
import kr.hhplus.be.server.domain.vo.product.ProductId;
import kr.hhplus.be.server.domain.vo.product.ProductStock;
import kr.hhplus.be.server.application.support.TestContainerSupport;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Map;

import static kr.hhplus.be.server.application.service.TestConstants.*;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest
@ActiveProfiles("integration-test")
class PlaceOrderSagaIntegrationTest extends TestContainerSupport {

    @Autowired PlaceOrderUseCase placeOrderUseCase;

    @Autowired OrderPersistenceAdapter orderPersistenceAdapter;
    @Autowired BalancePersistenceAdapter balancePersistenceAdapter;
    @Autowired CouponPersistenceAdapter couponPersistenceAdapter;
    @Autowired ProductPersistenceAdapter productPersistenceAdapter;

    @Autowired BalanceJpaRepository balanceJpaRepository;
    @Autowired CouponJpaRepository couponJpaRepository;
    @Autowired OrderJpaRepository orderJpaRepository;
    @Autowired OrderItemJpaRepository orderItemJpaRepository;
    @Autowired ProductJpaRepository productJpaRepository;

    PlaceOrderCommand command;

    Product product;
    ProductId productId;

    Coupon coupon;
    CouponId couponId;

    Balance balance;

    @BeforeEach
    void setUp() {
        balanceJpaRepository.deleteAll();
        couponJpaRepository.deleteAll();
        orderJpaRepository.deleteAll();
        orderItemJpaRepository.deleteAll();
        productJpaRepository.deleteAll();
    }

    @Test
    @DisplayName("(1) 주문 생성 → (2) 쿠폰 사용 → (3) 잔액 차감 → (4) 재고 차감 → (5) 주문 성공 완료")
    void 주문_성공() {
        // given
        setUpTestData(COUPON_NOT_USED, BALANCE_AMOUNT, PRODUCT_STOCK);

        // when
        var result = placeOrderUseCase.placeOrder(command);
        
        // then
        SagaTestUtils.verifySaga(result, () -> {
            assertOrderState(result.getOrderId(), ORDER_SUCCEEDED);
            assertCouponState(couponId, COUPON_USED);
            assertBalanceAmount(BALANCE_AMOUNT_DEDUCTED);
            assertProductStock(productId, PRODUCT_STOCK_DEDUCTED);
        });
    }

    @Test
    @DisplayName("(1) 주문 생성 → (2) 쿠폰 사용 실패 → (3) 주문 실패 완료")
    void 쿠폰_사용_불가() {
        // given
        setUpTestData(COUPON_USED, BALANCE_AMOUNT, PRODUCT_STOCK);

        // when
        var result = placeOrderUseCase.placeOrder(command);
        
        // then
        SagaTestUtils.verifySaga(result, () -> {
            assertOrderState(result.getOrderId(), ORDER_FAILED);
            assertCouponState(couponId, COUPON_USED);
            assertBalanceAmount(BALANCE_AMOUNT);
            assertProductStock(productId, PRODUCT_STOCK);

        });
    }
    
    @Test
    @DisplayName("(1) 주문 생성 → (2) 쿠폰 사용 → (3) 잔액 부족 → (4) 쿠폰 사용 취소 → (5) 주문 실패 완료")
    void 잔액_부족() {
        // given
        setUpTestData(COUPON_NOT_USED, BALANCE_AMOUNT_INSUFFICIENT, PRODUCT_STOCK);

        // when
        var result = placeOrderUseCase.placeOrder(command);
        
        // then
        SagaTestUtils.verifySaga(result, () -> {
            assertOrderState(result.getOrderId(), ORDER_FAILED);
            assertCouponState(result.getCouponId(), COUPON_NOT_USED);
            assertBalanceAmount(BALANCE_AMOUNT_INSUFFICIENT);
            assertProductStock(productId, PRODUCT_STOCK);
        });
    }
    
    @Test
    @DisplayName("(1) 주문 생성 → (2) 쿠폰 사용 → (3) 잔액 차감 → (4) 재고 부족 → (5) 잔액 차감 취소 → (6) 쿠폰 사용 취소 → (7) 주문 실패 완료")
    void 재고_부족() {
        // given
        setUpTestData(COUPON_NOT_USED, BALANCE_AMOUNT, PRODUCT_STOCK_INSUFFICIENT);

        // when
        var result = placeOrderUseCase.placeOrder(command);
        
        // then
        SagaTestUtils.verifySaga(result, () -> {
            assertOrderState(result.getOrderId(), ORDER_FAILED);
            assertCouponState(couponId, COUPON_NOT_USED);
            assertBalanceAmount(BALANCE_AMOUNT);
            assertProductStock(productId, PRODUCT_STOCK_INSUFFICIENT);
        });
    }

    void setUpTestData(CouponUsed couponUsed, BalanceAmount balanceAmount, ProductStock productStock) {
        coupon = couponPersistenceAdapter.saveCoupon(Coupon.create(
                USER_ID,
                COUPON_DISCOUNT_RATE,
                couponUsed
        ));
        balance = balancePersistenceAdapter.saveBalance(Balance.create(
                USER_ID,
                balanceAmount
        ));
        product = productPersistenceAdapter.saveProduct(Product.create(
                PRODUCT_NAME,
                PRODUCT_PRICE,
                productStock
        ));
        couponId = coupon.getCouponId();
        productId = product.getProductId();
        command = new PlaceOrderCommand(GUID, USER_ID, couponId, Map.of(productId, QUANTITY));
    }

    void assertOrderState(OrderId orderId, OrderSucceeded orderSucceeded) {
        orderPersistenceAdapter.loadOrder(orderId).ifPresent(loaded -> {
                    assertThat(loaded.getOrderFinished()).isEqualTo(ORDER_FINISHED);
                    assertThat(loaded.getOrderSucceeded()).isEqualTo(orderSucceeded);
        });
    }

    void assertCouponState(CouponId couponId, CouponUsed state) {
        couponPersistenceAdapter.loadCoupon(couponId).ifPresent(loaded -> {
            assertThat(loaded.getUsed()).isEqualTo(state);
        });
    }

    void assertBalanceAmount(BalanceAmount expected) {
        balancePersistenceAdapter.loadBalance(USER_ID).ifPresent(loaded -> {
            assertThat(loaded.getAmount()).isEqualTo(expected);
        });
    }

    void assertProductStock(ProductId productId, ProductStock expected) {
        Products loaded = productPersistenceAdapter.loadProducts(List.of(productId));
        assertThat(loaded.getProduct(productId).getStock()).isEqualTo(expected);
    }

}