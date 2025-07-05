package kr.hhplus.be.server.application.service.saga;

import kr.hhplus.be.server.domain.model.*;
import kr.hhplus.be.server.domain.vo.balance.BalanceAmount;
import kr.hhplus.be.server.domain.vo.coupon.CouponUsed;
import kr.hhplus.be.server.domain.vo.product.ProductStock;
import org.awaitility.Awaitility;
import org.awaitility.core.ThrowingRunnable;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;

import static kr.hhplus.be.server.application.service.TestConstants.*;
import static org.assertj.core.api.Assertions.assertThat;

public class SagaTestUtils {
    public static void verifySaga(Order order, ThrowingRunnable runnable) {
        assertThat(order).isNotNull();
        Awaitility.await()
                .pollDelay(Duration.ZERO)
                .pollInterval(Duration.ofMillis(100))
                .atMost(Duration.ofSeconds(2))
                .untilAsserted(runnable);
    }

    public static Coupon setUpCoupon(CouponUsed couponUsed) {
        return Coupon.create(
                COUPON_ID,
                USER_ID,
                COUPON_DISCOUNT_RATE,
                couponUsed,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }


    public static Balance setUpBalance(BalanceAmount balanceAmount) {
        return Balance.create(
                BALANCE_ID,
                USER_ID,
                balanceAmount,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }


    public static Products setUpProducts(ProductStock productStock) {
        return Products.create(Map.of(PRODUCT_ID, Product.create(
                PRODUCT_ID,
                PRODUCT_NAME,
                PRODUCT_PRICE,
                productStock,
                LocalDateTime.now(),
                LocalDateTime.now()
        )));
    }
}
