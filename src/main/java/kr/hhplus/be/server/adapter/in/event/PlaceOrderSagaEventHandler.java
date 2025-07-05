package kr.hhplus.be.server.adapter.in.event;

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
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Profile("!application-test")
@Component
@RequiredArgsConstructor
public class PlaceOrderSagaEventHandler {

    private final OrchestratePlaceOrderSagaUseCase placeOrderSagaOrchestrator;

    /*
     * 주문 생성 -> 쿠폰 사용
     */
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(OrderCreated event) {
        placeOrderSagaOrchestrator.handle(event);
    }

    /*
     * 쿠폰 사용됨 -> 잔고 차감
     */
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(CouponUsed event) {
        placeOrderSagaOrchestrator.handle(event);
    }

    /*
     * 잔고 차감됨 -> 재고 차감
     */
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(BalanceDeducted event) {
        placeOrderSagaOrchestrator.handle(event);
    }

    /*
     * 재고 차감됨 -> 주문 종료
     */
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(StockDeducted event) {
        placeOrderSagaOrchestrator.handle(event);
    }

    /*
     * 쿠폰 사용 실패 -> 주문 종료
     */
    @Async
    @EventListener
    public void handle(CouponUseFailed event) {
        placeOrderSagaOrchestrator.handle(event);
    }

    /*
     * 잔고 차감 실패 -> 쿠폰 사용 취소
     */
    @Async
    @EventListener
    public void handle(BalanceDeductionFailed event) {
        placeOrderSagaOrchestrator.handle(event);
    }
    /*
     * 재고 차감 실패 -> 잔고 차감 취소
     */
    @Async
    @EventListener
    public void handle(StockDeductionFailed event) {
        placeOrderSagaOrchestrator.handle(event);
    }

    /*
     * 잔고 차감 취소됨 -> 쿠폰 사용 취소
     */
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(BalanceDeductionCancelled event) {
        placeOrderSagaOrchestrator.handle(event);

    }

    /*
     * 쿠폰 사용 취소됨 -> 주문 종료
     */
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(CouponUseCancelled event) {
        placeOrderSagaOrchestrator.handle(event);
    }

}
