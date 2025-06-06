package kr.hhplus.be.server.application.service;

import kr.hhplus.be.server.application.port.in.PlaceOrderCommand;
import kr.hhplus.be.server.application.port.out.balance.LoadBalancePort;
import kr.hhplus.be.server.application.port.out.balance.UpdateBalancePort;
import kr.hhplus.be.server.application.port.out.coupon.LoadCouponPort;
import kr.hhplus.be.server.application.port.out.coupon.UpdateCouponPort;
import kr.hhplus.be.server.application.port.out.order.SaveOrderPort;
import kr.hhplus.be.server.application.port.out.product.ListProductPort;
import kr.hhplus.be.server.application.port.out.product.UpdateProductPort;
import kr.hhplus.be.server.domain.exception.BalanceInsufficientException;
import kr.hhplus.be.server.domain.exception.CouponAlreadyUsedException;
import kr.hhplus.be.server.domain.exception.ProductInsufficientException;
import kr.hhplus.be.server.domain.model.Balance;
import kr.hhplus.be.server.domain.model.Coupon;
import kr.hhplus.be.server.domain.model.Order;
import kr.hhplus.be.server.domain.model.Product;
import kr.hhplus.be.server.domain.vo.coupon.CouponDiscountRate;
import kr.hhplus.be.server.domain.vo.coupon.CouponId;
import kr.hhplus.be.server.domain.vo.order.OrderDiscountTotalPrice;
import kr.hhplus.be.server.domain.vo.orderitem.OrderItemQuantity;
import kr.hhplus.be.server.domain.vo.product.ProductId;
import kr.hhplus.be.server.domain.vo.product.ProductPrice;
import kr.hhplus.be.server.domain.vo.user.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlaceOrderServiceTest {

    @InjectMocks
    private PlaceOrderService placeOrderService;

    @Mock private Balance balance;
    @Mock private LoadBalancePort loadBalancePort;
    @Mock private UpdateBalancePort updateBalancePort;

    @Mock private Product product;
    @Mock private ProductPrice productPrice;
    @Mock private ListProductPort listProductPort;
    @Mock private UpdateProductPort updateProductPort;

    @Mock private Coupon coupon;
    @Mock private CouponDiscountRate discountRate;
    @Mock private LoadCouponPort loadCouponPort;
    @Mock private UpdateCouponPort updateCouponPort;

    @Mock private Order order;
    @Mock private OrderDiscountTotalPrice discountTotalPrice;
    @Mock private SaveOrderPort saveOrderPort;

    private UserId userId;
    private ProductId productId;
    private OrderItemQuantity quantity;
    private CouponId couponId;
    private PlaceOrderCommand command;
    private Map<ProductId, Product> productMap;

    @BeforeEach
    void setUp() {
        userId = new UserId(1L);
        productId = new ProductId(1L);
        quantity = new OrderItemQuantity(2);
        couponId = new CouponId(1L);
        productMap = Map.of(productId, product);
        command = new PlaceOrderCommand(
                userId,
                Map.of(productId, quantity),
                couponId
        );
    }

    @Test
    @DisplayName("주문 생성에 성공한다")
    void placeOrderSuccess() {
        // given
        // 도메인 조회
        doReturn(Optional.of(coupon)).when(loadCouponPort).loadCoupon(couponId);
        doReturn(Optional.of(balance)).when(loadBalancePort).loadBalance(userId);
        doReturn(productMap).when(listProductPort).listProducts(command.productIds());

        // 주문 생성
        doReturn(productPrice).when(product).getPrice();
        doReturn(discountRate).when(coupon).getDiscountRate();
        doReturn(order).when(saveOrderPort).saveOrder(any());

        // 쿠폰 사용 및 업데이트
        doNothing().when(coupon).use();
        doReturn(coupon).when(updateCouponPort).updateCoupon(coupon);

        // 잔액 차감 및 업데이트
        doReturn(discountTotalPrice).when(order).getDiscountTotalPrice();
        doNothing().when(balance).deduct(discountTotalPrice);
        doReturn(balance).when(updateBalancePort).updateBalance(balance);

        // 상품 재고 차감 및 업데이트
        doReturn(quantity).when(order).getOrderItemQuantity(productId);
        doNothing().when(product).decreaseStock(quantity);
        doReturn(productMap.values()).when(updateProductPort).updateProducts(productMap.values());

        // when
        Order result = placeOrderService.placeOrder(command);

        // then
        assertThat(result).isEqualTo(order);

        // 조회 여부 검증
        verify(loadCouponPort).loadCoupon(couponId);
        verify(loadBalancePort).loadBalance(userId);
        verify(listProductPort).listProducts(command.productIds());

        // 주문 저장 검증
        verify(saveOrderPort).saveOrder(any());

        // 쿠폰 사용 및 업데이트 검증
        verify(coupon).use();
        verify(updateCouponPort).updateCoupon(coupon);

        // 잔액 차감 및 업데이트 검증
        verify(balance).deduct(discountTotalPrice);
        verify(updateBalancePort).updateBalance(balance);

        // 상품 재고 차감 및 업데이트 검증
        verify(product).decreaseStock(quantity);
        verify(updateProductPort).updateProducts(any());
    }

    @Test
    @DisplayName("쿠폰이 이미 사용되었으면 예외가 발생한다")
    void throwExceptionWhenCouponAlreadyUsed() {
        // given
        // 도메인 조회
        doReturn(Optional.of(coupon)).when(loadCouponPort).loadCoupon(couponId);
        doReturn(Optional.of(balance)).when(loadBalancePort).loadBalance(userId);
        doReturn(productMap).when(listProductPort).listProducts(command.productIds());

        // 주문 생성
        doReturn(productPrice).when(product).getPrice();
        doReturn(discountRate).when(coupon).getDiscountRate();
        doReturn(order).when(saveOrderPort).saveOrder(any());

        // 쿠폰 사용 및 업데이트
        doThrow(new CouponAlreadyUsedException()).when(coupon).use();

        // when & then
        assertThatThrownBy(() -> placeOrderService.placeOrder(command))
                .isInstanceOf(CouponAlreadyUsedException.class)
                .hasMessageContaining("이미 사용된 쿠폰입니다.");

        // 쿠폰 조회만 수행되고 다른 작업은 수행되지 않음
        verifyNoMoreInteractions(coupon, balance, product);
        verifyNoInteractions(updateProductPort, updateBalancePort, updateCouponPort);
    }

    @Test
    @DisplayName("잔액이 부족하면 예외가 발생한다")
    void throwExceptionWhenInsufficientBalance() {
        // given
        // 도메인 조회
        doReturn(Optional.of(coupon)).when(loadCouponPort).loadCoupon(couponId);
        doReturn(Optional.of(balance)).when(loadBalancePort).loadBalance(userId);
        doReturn(productMap).when(listProductPort).listProducts(command.productIds());

        // 주문 생성
        doReturn(productPrice).when(product).getPrice();
        doReturn(discountRate).when(coupon).getDiscountRate();
        doReturn(order).when(saveOrderPort).saveOrder(any());

        // 쿠폰 사용 및 업데이트
        doNothing().when(coupon).use();
        doReturn(coupon).when(updateCouponPort).updateCoupon(coupon);

        // 잔액 차감 및 업데이트
        doReturn(discountTotalPrice).when(order).getDiscountTotalPrice();
        doThrow(new BalanceInsufficientException()).when(balance).deduct(discountTotalPrice);

        // when & then
        assertThatThrownBy(() -> placeOrderService.placeOrder(command))
                .isInstanceOf(BalanceInsufficientException.class)
                .hasMessageContaining("잔액이 부족합니다");

        // 쿠폰, 잔액, 상품 조회만 수행되고 다른 작업은 수행되지 않음
        verifyNoMoreInteractions(coupon, balance, product);
        verifyNoInteractions(updateProductPort, updateBalancePort);
    }

    @Test
    @DisplayName("재고가 부족하면 예외가 발생한다")
    void throwExceptionWhenInsufficientStock() {
        // given
        // 도메인 조회
        doReturn(Optional.of(coupon)).when(loadCouponPort).loadCoupon(couponId);
        doReturn(Optional.of(balance)).when(loadBalancePort).loadBalance(userId);
        doReturn(productMap).when(listProductPort).listProducts(command.productIds());

        // 주문 생성
        doReturn(productPrice).when(product).getPrice();
        doReturn(discountRate).when(coupon).getDiscountRate();
        doReturn(order).when(saveOrderPort).saveOrder(any());

        // 쿠폰 사용 및 업데이트
        doNothing().when(coupon).use();
        doReturn(coupon).when(updateCouponPort).updateCoupon(coupon);

        // 잔액 차감 및 업데이트
        doReturn(discountTotalPrice).when(order).getDiscountTotalPrice();
        doNothing().when(balance).deduct(discountTotalPrice);
        doReturn(balance).when(updateBalancePort).updateBalance(balance);

        // 상품 재고 차감 및 업데이트
        doReturn(quantity).when(order).getOrderItemQuantity(productId);
        doThrow(new ProductInsufficientException()).when(product).decreaseStock(quantity);

        // when & then
        assertThatThrownBy(() -> placeOrderService.placeOrder(command))
                .isInstanceOf(ProductInsufficientException.class)
                .hasMessageContaining("재고가 부족합니다");

        // 쿠폰, 잔액, 상품 조회만 수행되고 다른 작업은 수행되지 않음
        verifyNoMoreInteractions(coupon, balance, product);
        verifyNoInteractions(updateProductPort);
    }
}