package kr.hhplus.be.server.application.service;

import kr.hhplus.be.server.domain.event.DomainEvent;
import kr.hhplus.be.server.domain.event.product.StockDeductionCancelled;
import kr.hhplus.be.server.domain.vo.order.Guid;
import kr.hhplus.be.server.domain.vo.balance.BalanceAmount;
import kr.hhplus.be.server.domain.vo.balance.BalanceId;
import kr.hhplus.be.server.domain.vo.coupon.CouponDiscountRate;
import kr.hhplus.be.server.domain.vo.coupon.CouponId;
import kr.hhplus.be.server.domain.vo.coupon.CouponUsed;
import kr.hhplus.be.server.domain.vo.order.*;
import kr.hhplus.be.server.domain.vo.orderitem.OrderItemQuantity;
import kr.hhplus.be.server.domain.vo.product.ProductId;
import kr.hhplus.be.server.domain.vo.product.ProductName;
import kr.hhplus.be.server.domain.vo.product.ProductPrice;
import kr.hhplus.be.server.domain.vo.product.ProductStock;
import kr.hhplus.be.server.domain.vo.user.UserId;

import java.util.List;
import java.util.Map;

public class TestConstants {

    public static final OrderItemQuantity QUANTITY = new OrderItemQuantity(1);

    public static final CouponUsed COUPON_USED = CouponUsed.USED;
    public static final CouponUsed COUPON_NOT_USED = CouponUsed.NOT_USED;
    public static final CouponDiscountRate COUPON_DISCOUNT_RATE = new CouponDiscountRate(0.1);

    public static final ProductName PRODUCT_NAME = new ProductName("테스트 상품");
    public static final ProductPrice PRODUCT_PRICE = new ProductPrice(10000L);
    public static final ProductStock PRODUCT_STOCK = new ProductStock(10L);
    public static final ProductStock PRODUCT_STOCK_INSUFFICIENT = new ProductStock(0L);
    public static final ProductStock PRODUCT_STOCK_DEDUCTED = new ProductStock(
            PRODUCT_STOCK.value() - QUANTITY.value()
    );

    public static final OrderFinished ORDER_FINISHED = OrderFinished.FINISHED;
    public static final OrderFinished ORDER_NOT_FINISHED = OrderFinished.NOT_FINISHED;
    public static final OrderSucceeded ORDER_SUCCEEDED = OrderSucceeded.SUCCEEDED;
    public static final OrderSucceeded ORDER_FAILED = OrderSucceeded.FAILED;
    public static final OrderOriginalTotalPrice ORDER_ORIGINAL_PRICE = new OrderOriginalTotalPrice(
            PRODUCT_PRICE.value() * QUANTITY.value()
    );
    public static final OrderDiscountTotalPrice ORDER_DISCOUNT_PRICE = new OrderDiscountTotalPrice(
            Math.round(ORDER_ORIGINAL_PRICE.value() * (1.0 - COUPON_DISCOUNT_RATE.value()))
    );

    public static final BalanceAmount BALANCE_AMOUNT = new BalanceAmount(20000L);
    public static final BalanceAmount BALANCE_AMOUNT_INSUFFICIENT = new BalanceAmount(0L);
    public static final BalanceAmount BALANCE_AMOUNT_ADDED = new BalanceAmount(
            BALANCE_AMOUNT.value() + BALANCE_AMOUNT.value()
    );
    public static final BalanceAmount BALANCE_AMOUNT_DEDUCTED = new BalanceAmount(
            BALANCE_AMOUNT.value() - ORDER_DISCOUNT_PRICE.value()
    );

    public static final Guid GUID = new Guid(System.currentTimeMillis());
    public static final OrderId ORDER_ID = new OrderId(1L);
    public static final UserId USER_ID = new UserId(1L);
    public static final CouponId COUPON_ID = new CouponId(1L);
    public static final ProductId PRODUCT_ID = new ProductId(1L);
    public static final BalanceId BALANCE_ID = new BalanceId(1L);

    public static final Map<ProductId, OrderItemQuantity> ORDER_ITEM_QUANTITIES = Map.of(PRODUCT_ID, QUANTITY);
    public static final List<ProductId> PRODUCT_IDS = List.of(PRODUCT_ID);


    public static final DomainEvent EVENT = new StockDeductionCancelled(
            ORDER_ID,
            COUPON_ID,
            USER_ID,
            ORDER_DISCOUNT_PRICE,
            ORDER_ITEM_QUANTITIES,
            PRODUCT_IDS
    );
}
