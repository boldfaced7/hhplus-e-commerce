package kr.hhplus.be.server.application.service.saga;

import kr.hhplus.be.server.domain.model.*;
import kr.hhplus.be.server.domain.vo.balance.BalanceAmount;
import kr.hhplus.be.server.domain.vo.coupon.CouponUsed;
import kr.hhplus.be.server.domain.vo.product.ProductStock;
import org.awaitility.Awaitility;
import org.awaitility.core.ThrowingRunnable;
import org.springframework.test.util.ReflectionTestUtils;

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

    public static OrderItem setUpOrderItem() {
        return new OrderItem(
                ORDER_ITEM_ID,
                ORDER_ID,
                PRODUCT_ID,
                QUANTITY,
                ORDER_ITEM_ORIGINAL_PRICE,
                ORDER_ITEM_DISCOUNT_PRICE,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    public static OrderItems setUpOrderItems() {
        return new OrderItems(Map.of(
                PRODUCT_ID, setUpOrderItem()
        ));
    }

    public static Order setUpOrder() {
        var order = new Order(
                ORDER_ID,
                UUID_RANDOM,
                USER_ID,
                COUPON_ID,
                setUpOrderItems(),
                ORDER_ORIGINAL_PRICE,
                ORDER_DISCOUNT_PRICE,
                ORDER_NOT_FINISHED,
                ORDER_FAILED,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        ReflectionTestUtils.invokeMethod(order, "markOrderCreated");
        return order;
    }


    public static Coupon setUpCoupon(CouponUsed couponUsed) {
        return new Coupon(
                COUPON_ID,
                USER_ID,
                COUPON_DISCOUNT_RATE,
                couponUsed,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }


    public static Balance setUpBalance(BalanceAmount balanceAmount) {
        return new Balance(
                BALANCE_ID,
                USER_ID,
                balanceAmount,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }


    public static Products setUpProducts(ProductStock productStock) {
        return Products.create(Map.of(PRODUCT_ID, new Product(
                PRODUCT_ID,
                PRODUCT_NAME,
                PRODUCT_PRICE,
                productStock,
                LocalDateTime.now(),
                LocalDateTime.now()
        )));
    }
}
