package kr.hhplus.be.server.application.service;

import jakarta.transaction.Transactional;
import kr.hhplus.be.server.application.port.in.PlaceOrderCommand;
import kr.hhplus.be.server.application.port.in.PlaceOrderUseCase;
import kr.hhplus.be.server.application.port.out.coupon.LoadCouponPort;
import kr.hhplus.be.server.application.port.out.event.PublishEventPort;
import kr.hhplus.be.server.application.port.out.order.SaveOrderPort;
import kr.hhplus.be.server.application.port.out.product.LoadProductsPort;
import kr.hhplus.be.server.domain.exception.CouponNotFoundException;
import kr.hhplus.be.server.domain.model.Coupon;
import kr.hhplus.be.server.domain.model.Order;
import kr.hhplus.be.server.domain.model.Products;
import kr.hhplus.be.server.domain.vo.coupon.CouponId;
import kr.hhplus.be.server.domain.vo.product.ProductId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class PlaceOrderService implements PlaceOrderUseCase {

    private final PublishEventPort publishEventPort;
    private final LoadProductsPort loadProductsPort;
    private final LoadCouponPort loadCouponPort;
    private final SaveOrderPort saveOrderPort;


    @Override
    @Transactional
    public Order placeOrder(PlaceOrderCommand command) {
        var products = getProducts(command.productIds()); // 상품 조회
        var coupon   = getCoupon(command.couponId());     // 쿠폰 조회

        return createOrder(command, products, coupon);
    }

    private Products getProducts(Collection<ProductId> productIds) {
        var loaded = loadProductsPort.loadProducts(productIds);
        loaded.checkAllProductExistence(); // 조회 실패한 제품이 있는 경우 예외 발생
        return loaded;
    }

    private Coupon getCoupon(CouponId couponId) {
        return loadCouponPort.loadCoupon(couponId)
                .orElseThrow(CouponNotFoundException::new);
    }

    private Order createOrder(
            PlaceOrderCommand command,
            Products products,
            Coupon coupon
    ) {
        var toBeSaved = Order.create(
                command.guid(),
                command.userId(),
                command.couponId(),
                command.orderItemQuantities(),
                products.getProductPrices(),
                coupon.getDiscountRate()
        );
        var saved = saveOrderPort.saveOrder(toBeSaved);
        saved.markOrderCreated();
        publishEventPort.publishEvents(saved);

        return saved;
    }

}
