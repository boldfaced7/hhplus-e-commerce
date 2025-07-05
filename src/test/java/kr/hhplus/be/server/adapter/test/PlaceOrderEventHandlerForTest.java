package kr.hhplus.be.server.adapter.test;

import kr.hhplus.be.server.application.port.in.OrchestratePlaceOrderSagaUseCase;
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
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Profile("application-test")
@Component
@RequiredArgsConstructor
public class PlaceOrderEventHandlerForTest {

    private final OrchestratePlaceOrderSagaUseCase placeOrderSagaOrchestrator;

    /*
     * 주문 생성 -> 쿠폰 사용
     */
    @Async
    @EventListener
    public void handle(OrderCreated event) {
        logEvent(event);
        placeOrderSagaOrchestrator.handle(event);
    }

    /*
     * 쿠폰 사용됨 -> 잔고 차감
     */
    @Async
    @EventListener
    public void handle(CouponUsed event) {
        logEvent(event);
        placeOrderSagaOrchestrator.handle(event);
    }

    /*
     * 잔고 차감됨 -> 재고 차감
     */
    @Async
    @EventListener
    public void handle(BalanceDeducted event) {
        logEvent(event);
        placeOrderSagaOrchestrator.handle(event);
    }

    /*
     * 재고 차감됨 -> 주문 종료
     */
    @Async
    @EventListener
    public void handle(StockDeducted event) {
        logEvent(event);
        placeOrderSagaOrchestrator.handle(event);
    }

    /*
     * 쿠폰 사용 실패 -> 주문 종료
     */
    @Async
    @EventListener
    public void handle(CouponUseFailed event) {
        logEvent(event);
        placeOrderSagaOrchestrator.handle(event);
    }

    /*
     * 잔고 차감 실패 -> 쿠폰 사용 취소
     */
    @Async
    @EventListener
    public void handle(BalanceDeductionFailed event) {
        logEvent(event);
        placeOrderSagaOrchestrator.handle(event);
    }
    /*
     * 재고 차감 실패 -> 잔고 차감 취소
     */
    @Async
    @EventListener
    public void handle(StockDeductionFailed event) {
        logEvent(event);
        placeOrderSagaOrchestrator.handle(event);
    }

    /*
     * 잔고 차감 취소됨 -> 쿠폰 사용 취소
     */
    @Async
    @EventListener
    public void handle(BalanceDeductionCancelled event) {
        logEvent(event);
        placeOrderSagaOrchestrator.handle(event);
    }

    /*
     * 쿠폰 사용 취소됨 -> 주문 종료
     */
    @Async
    @EventListener
    public void handle(CouponUseCancelled event) {
        logEvent(event);
        placeOrderSagaOrchestrator.handle(event);
    }

    private void logEvent(Object event) {
        log.info("{}", event);
    }
}
