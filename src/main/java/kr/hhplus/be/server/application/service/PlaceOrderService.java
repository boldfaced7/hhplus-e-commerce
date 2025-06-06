package kr.hhplus.be.server.application.service;

import jakarta.transaction.Transactional;
import kr.hhplus.be.server.application.port.in.PlaceOrderCommand;
import kr.hhplus.be.server.application.port.in.PlaceOrderUseCase;
import kr.hhplus.be.server.application.port.out.balance.LoadBalancePort;
import kr.hhplus.be.server.application.port.out.balance.UpdateBalancePort;
import kr.hhplus.be.server.application.port.out.coupon.LoadCouponPort;
import kr.hhplus.be.server.application.port.out.coupon.UpdateCouponPort;
import kr.hhplus.be.server.application.port.out.order.SaveOrderPort;
import kr.hhplus.be.server.application.port.out.product.ListProductPort;
import kr.hhplus.be.server.application.port.out.product.UpdateProductPort;
import kr.hhplus.be.server.domain.exception.BalanceNotFoundException;
import kr.hhplus.be.server.domain.exception.CouponNotFoundException;
import kr.hhplus.be.server.domain.model.Balance;
import kr.hhplus.be.server.domain.model.Coupon;
import kr.hhplus.be.server.domain.model.Order;
import kr.hhplus.be.server.domain.model.Product;
import kr.hhplus.be.server.domain.vo.coupon.CouponId;
import kr.hhplus.be.server.domain.vo.product.ProductId;
import kr.hhplus.be.server.domain.vo.user.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlaceOrderService implements PlaceOrderUseCase {

    private final LoadBalancePort loadBalancePort;
    private final UpdateBalancePort updateBalancePort;

    private final ListProductPort listProductPort;
    private final UpdateProductPort updateProductPort;

    private final LoadCouponPort loadCouponPort;
    private final UpdateCouponPort updateCouponPort;

    private final SaveOrderPort saveOrderPort;

    @Override
    @Transactional
    public Order placeOrder(PlaceOrderCommand command) {
        var coupon   = getCoupon(command.couponId());          // 쿠폰 조회
        var balance  = getBalance(command.userId());           // 잔액 조회
        var products = getProducts(command.productIds());      // 상품 조회
        var order    = createOrder(command, products, coupon); // 주문 생성

        useCoupon(coupon);               // 쿠폰 사용 처리(이미 사용된 쿠폰인 경우 예외 발생)
        deductBalance(balance, order);   // 잔액 차감(잔액 부족 시 예외 발생)
        decreaseStocks(products, order); // 재고 차감(재고 부족 시 예외 발생)

        return order;
    }

    /*
     * 쿠폰 조회
     * 조회 결과가 없는 경우 예외 발생
     */
    private Coupon getCoupon(CouponId couponId) {
        return loadCouponPort.loadCoupon(couponId)
                .orElseThrow(CouponNotFoundException::new);
    }

    /*
     * 잔액 조회
     * 조회 결과가 없는 경우 예외 발생
     */
    private Balance getBalance(UserId userId) {
        return loadBalancePort.loadBalance(userId)
                .orElseThrow(BalanceNotFoundException::new);
    }

    /*
     * 상품 조회
     */
    private Map<ProductId, Product> getProducts(Collection<ProductId> productIds) {
        return listProductPort.listProducts(productIds);
    }

    /*
     * 주문 생성
     */
    private Order createOrder(PlaceOrderCommand command, Map<ProductId, Product> products, Coupon coupon) {
        var productPrices = products.keySet().stream()
                .collect(Collectors.toMap(
                        Function.identity(),
                        productId -> products.get(productId).getPrice()
                ));

        Order order = Order.create(
                command.userId(),
                command.couponId(),
                command.orderItemQuantities(),
                productPrices,
                coupon.getDiscountRate()
        );
        return saveOrderPort.saveOrder(order);
    }

    /*
     * 쿠폰 사용 처리
     * 이미 사용된 쿠폰인 경우 예외 발생
     */
    private void useCoupon(Coupon coupon) {
        coupon.use();
        updateCouponPort.updateCoupon(coupon);
    }

    /*
     * 잔액 차감
     * 잔액 부족 시 예외 발생
     */
    private void deductBalance(Balance balance, Order order) {
        balance.deduct(order.getDiscountTotalPrice());
        updateBalancePort.updateBalance(balance);
    }

    /*
     * 재고 차감
     * 재고 부족 시 예외 발생
     */
    private void decreaseStocks(Map<ProductId, Product> products, Order order) {
        products.forEach((productId, product) ->
                product.decreaseStock(order.getOrderItemQuantity(productId))
        );
        updateProductPort.updateProducts(products.values());
    }

}
