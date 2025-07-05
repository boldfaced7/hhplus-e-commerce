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
import kr.hhplus.be.server.domain.vo.order.Guid;
import kr.hhplus.be.server.domain.vo.product.ProductId;
import kr.hhplus.be.server.domain.vo.user.UserId;
import kr.hhplus.be.server.application.support.TestContainerSupport;
import lombok.extern.slf4j.Slf4j;
import org.awaitility.Awaitility;
import org.awaitility.core.ThrowingRunnable;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;

import static kr.hhplus.be.server.application.service.TestConstants.*;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest
@ActiveProfiles("integration-test")
class PlaceOrderSagaConcurrencyTest extends TestContainerSupport {

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

    private ProductId productId;

    @BeforeEach
    void setUpProduct() {
        Product product = productPersistenceAdapter.saveProduct(
                Product.create(PRODUCT_NAME, PRODUCT_PRICE, PRODUCT_STOCK)
        );
        productId = product.getProductId();
    }

    @AfterEach
    void clearRepository() {
        balanceJpaRepository.deleteAll();
        couponJpaRepository.deleteAll();
        orderJpaRepository.deleteAll();
        orderItemJpaRepository.deleteAll();
        productJpaRepository.deleteAll();
    }

    @Test
    @DisplayName("재고 감소 동시성 제어 테스트 - 다수 사용자가 동시에 같은 상품을 주문할 때 재고가 정확히 차감되어야 함")
    void 재고_감소_동시성_제어() throws InterruptedException {
        // given & when
        // 재고보다 많은 수의 동시 요청
        runConcurrentOrders(15, this::setUpUserData);

        // then
        verify(() -> productJpaRepository.findById(productId.value())
                .ifPresent(product -> assertThat(product.getStock()).isEqualTo(0)));
    }

    @Test
    @DisplayName("한 사용자의 동시 주문 시 잔액 차감 동시성 제어 테스트 - 잔액이 음수가 되지 않아야 함")
    void 한_사용자_동시_주문_잔액_차감_동시성_제어() throws InterruptedException {
        // given
        balancePersistenceAdapter.saveBalance(Balance.create(USER_ID, BALANCE_AMOUNT));
        int threadCount = 5; // 잔액으로 처리 가능한 주문 수보다 많은 요청

        // when
        runConcurrentOrders(threadCount, i -> setUpCoupon());

        // then
        verify(() -> balanceJpaRepository.findByUserId(USER_ID.value())
                .ifPresent(balance -> assertThat(balance.getAmount()).isEqualTo(2000)));
    }

    private PlaceOrderCommand setUpUserData(long userIndex) {
        UserId userId = new UserId(userIndex + 1);
        Coupon userCoupon = couponPersistenceAdapter.saveCoupon(Coupon.create(
                userId,
                COUPON_DISCOUNT_RATE,
                COUPON_NOT_USED
        ));
        balancePersistenceAdapter.saveBalance(Balance.create(userId, BALANCE_AMOUNT));

        return new PlaceOrderCommand(
                new Guid(System.currentTimeMillis()),
                userId,
                userCoupon.getCouponId(),
                Map.of(productId, QUANTITY)
        );
    }

    private PlaceOrderCommand setUpCoupon() {
        Coupon coupon = couponPersistenceAdapter.saveCoupon(Coupon.create(
                USER_ID,
                COUPON_DISCOUNT_RATE,
                COUPON_NOT_USED
        ));
        return new PlaceOrderCommand(
                new Guid(System.currentTimeMillis()),
                USER_ID,
                coupon.getCouponId(),
                Map.of(productId, QUANTITY)
        );
    }

    private void verify(ThrowingRunnable assertion) {
        Awaitility.await()
                .pollDelay(Duration.ZERO)
                .pollInterval(Duration.ofMillis(100))
                .atMost(Duration.ofSeconds(5))
                .untilAsserted(assertion);
    }

    /**
     * 동시성 테스트를 위한 공통 메소드
     * @param threadCount 동시 실행할 스레드 수
     * @param commandFunction 각 스레드에서 실행할 주문 작업
     * @throws InterruptedException 인터럽트 예외
     */
    private void runConcurrentOrders(
            int threadCount,
            Function<Integer, PlaceOrderCommand> commandFunction
    ) throws InterruptedException {

        CountDownLatch latch = new CountDownLatch(threadCount);
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        try {
            for (int i = 0; i < threadCount; i++) {
                final int userIndex = i;

                executor.submit(() -> {
                    try {
                        PlaceOrderCommand command = commandFunction.apply(userIndex);
                        placeOrderUseCase.placeOrder(command);
                    } catch (Exception e) {
                        log.error("주문 요청 중 오류 발생: {}", e.getMessage());
                    } finally {
                        latch.countDown();
                    }
                });
            }
            latch.await();
        } finally {
            executor.shutdown();
        }
    }

}