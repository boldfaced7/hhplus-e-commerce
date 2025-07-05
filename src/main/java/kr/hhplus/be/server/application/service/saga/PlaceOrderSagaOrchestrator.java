package kr.hhplus.be.server.application.service.saga;

import kr.hhplus.be.server.application.port.in.OrchestratePlaceOrderSagaUseCase;
import kr.hhplus.be.server.application.service.saga.balance.CancelBalanceDeductionService;
import kr.hhplus.be.server.application.service.saga.balance.DeductBalanceService;
import kr.hhplus.be.server.application.service.saga.coupon.CancelCouponUseService;
import kr.hhplus.be.server.application.service.saga.coupon.UseCouponService;
import kr.hhplus.be.server.application.service.saga.order.CompleteOrderService;
import kr.hhplus.be.server.application.service.saga.product.DeductStockService;
import kr.hhplus.be.server.domain.event.balance.BalanceDeducted;
import kr.hhplus.be.server.domain.event.balance.BalanceDeductionCancelled;
import kr.hhplus.be.server.domain.event.balance.BalanceDeductionFailed;
import kr.hhplus.be.server.domain.event.coupon.CouponUseCancelled;
import kr.hhplus.be.server.domain.event.coupon.CouponUseFailed;
import kr.hhplus.be.server.domain.event.coupon.CouponUsed;
import kr.hhplus.be.server.domain.event.order.OrderCreated;
import kr.hhplus.be.server.domain.event.product.StockDeducted;
import kr.hhplus.be.server.domain.event.product.StockDeductionFailed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PlaceOrderSagaOrchestrator implements OrchestratePlaceOrderSagaUseCase {

    private final UseCouponService useCouponService;
    private final DeductBalanceService deductBalanceService;
    private final DeductStockService deductStockService;

    private final CancelCouponUseService cancelCouponUseService;
    private final CancelBalanceDeductionService cancelBalanceDeductionService;

    private final CompleteOrderService completeOrderService;

    // 주문 생성됨: 쿠폰 사용 서비스 호출
    public void handle(OrderCreated event) {
        useCouponService.useCoupon(event);
    }

    // 쿠폰 사용됨: 잔액 차감 서비스 호출
    public void handle(CouponUsed event) {
        deductBalanceService.deductBalance(event);
    }

    // 잔액 차감됨: 재고 차감 서비스 호출
    public void handle(BalanceDeducted event) {
        deductStockService.deductStock(event);
    }

    // 재고 차감됨: 주문 종결 서비스 호출
    public void handle(StockDeducted event) {
        completeOrderService.succeedOrder(event.orderId());
    }

    // 쿠폰 사용 실패: 주문 종결 서비스 호출
    public void handle(CouponUseFailed event) {
        completeOrderService.failOrder(event.orderId());
    }

    // 잔액 차감 실패: 쿠폰 사용 취소 서비스 호출
    public void handle(BalanceDeductionFailed event) {
        cancelCouponUseService.cancelCouponUse(event);
    }

    // 재고 차감 실패: 잔액 차감 취소 서비스 호출
    public void handle(StockDeductionFailed event) {
        cancelBalanceDeductionService.cancelBalanceDeduction(event);
    }

    // 잔액 차감 취소됨: 쿠폰 사용 취소 서비스 호출
    public void handle(BalanceDeductionCancelled event) {
        cancelCouponUseService.cancelCouponUse(event);
    }

    // 쿠폰 사용 취소됨: 주문 종결 서비스 호출
    public void handle(CouponUseCancelled event) {
        completeOrderService.failOrder(event.orderId());
    }

}
