package kr.hhplus.be.server.application.service;

import kr.hhplus.be.server.application.port.in.PlaceOrderCommand;
import kr.hhplus.be.server.application.port.out.balance.SaveBalancePort;
import kr.hhplus.be.server.application.port.out.coupon.SaveCouponPort;
import kr.hhplus.be.server.application.port.out.product.SaveProductPort;
import kr.hhplus.be.server.domain.model.Balance;
import kr.hhplus.be.server.domain.model.Coupon;
import kr.hhplus.be.server.domain.model.Product;
import kr.hhplus.be.server.domain.vo.balance.BalanceAmount;
import kr.hhplus.be.server.domain.vo.coupon.CouponDiscountRate;
import kr.hhplus.be.server.domain.vo.coupon.CouponUsed;
import kr.hhplus.be.server.domain.vo.orderitem.OrderItemQuantity;
import kr.hhplus.be.server.domain.vo.product.ProductName;
import kr.hhplus.be.server.domain.vo.product.ProductPrice;
import kr.hhplus.be.server.domain.vo.product.ProductStock;
import kr.hhplus.be.server.domain.vo.user.UserId;
import kr.hhplus.be.server.support.TestContainerSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
class PlaceOrderServiceIntegrationTest extends TestContainerSupport {
    @Autowired
    private PlaceOrderService placeOrderService;
    
    @Autowired
    private SaveProductPort saveProductPort;
    
    @Autowired
    private SaveBalancePort saveBalancePort;
    
    @Autowired
    private SaveCouponPort saveCouponPort;

    private UserId userId;
    private BalanceAmount initialAmount;
    private Balance balance;
    private Product product;
    private Coupon coupon;

    @BeforeEach
    void setUp() {
        userId = new UserId(System.currentTimeMillis());
        initialAmount = new BalanceAmount(100000L);
        balance = Balance.create(userId, initialAmount);
        balance = saveBalancePort.saveBalance(balance);

        product = Product.create(
                new ProductName("상품1"),
                new ProductPrice(10000L),
                new ProductStock(100L)
        );
        product = saveProductPort.saveProduct(product);

        coupon = Coupon.create(
                userId,
                new CouponDiscountRate(0.1),
                new CouponUsed(false)
        );
        coupon = saveCouponPort.saveCoupon(coupon);
    }

    @Test
    @DisplayName("쿠폰을 사용하여 주문이 정상적으로 이루어진다.")
    void placeOrderWithCoupon() {
        // given
        var command = new PlaceOrderCommand(
                userId,
                Map.of(product.getProductId(), new OrderItemQuantity(5)),
                coupon.getCouponId()
        );

        // when
        var order = placeOrderService.placeOrder(command);

        // then
        assertThat(order.getUserId().value()).isEqualTo(userId.value());
        assertThat(order.getCouponId()).isEqualTo(coupon.getCouponId());
        assertThat(order.getOrderItems()).hasSize(1);
        assertThat(order.getOrderItems().get(product.getProductId()).getQuantity().value()).isEqualTo(5);
    }

} 