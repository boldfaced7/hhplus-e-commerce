package kr.hhplus.be.server.application.port.in;

import kr.hhplus.be.server.domain.event.balance.BalanceDeducted;
import kr.hhplus.be.server.domain.event.balance.BalanceDeductionCancelled;
import kr.hhplus.be.server.domain.event.balance.BalanceDeductionFailed;
import kr.hhplus.be.server.domain.event.coupon.CouponUseCancelled;
import kr.hhplus.be.server.domain.event.coupon.CouponUseFailed;
import kr.hhplus.be.server.domain.event.coupon.CouponUsed;
import kr.hhplus.be.server.domain.event.order.OrderCreated;
import kr.hhplus.be.server.domain.event.product.StockDeducted;
import kr.hhplus.be.server.domain.event.product.StockDeductionFailed;

/**
 * 주문 생성부터 완료까지의 Saga 패턴을 관리하는 오케스트레이터 Port
 * 
 * 주문 처리 과정:
 * 1. 주문 생성 (OrderCreated)
 * 2. 쿠폰 사용 (CouponUsed)
 * 3. 잔고 차감 (BalanceDeducted)
 * 4. 재고 차감 (StockDeducted)
 * 5. 주문 완료
 * 
 * 실패 시 보상 트랜잭션:
 * - 쿠폰 사용 실패 → 주문 실패
 * - 잔고 차감 실패 → 쿠폰 사용 취소
 * - 재고 차감 실패 → 잔고 차감 취소
 * - 잔고 차감 취소 → 쿠폰 사용 취소
 * - 쿠폰 사용 취소 → 주문 실패
 */
public interface OrchestratePlaceOrderSagaUseCase {

    /**
     * 주문 생성 이벤트 처리
     * 주문이 생성되면 쿠폰 사용 프로세스를 시작합니다.
     * 
     * @param event 주문 생성 이벤트
     */
    void handle(OrderCreated event);

    /**
     * 쿠폰 사용 완료 이벤트 처리
     * 쿠폰이 성공적으로 사용되면 잔고 차감 프로세스를 시작합니다.
     * 
     * @param event 쿠폰 사용 완료 이벤트
     */
    void handle(CouponUsed event);

    /**
     * 잔고 차감 완료 이벤트 처리
     * 잔고가 성공적으로 차감되면 재고 차감 프로세스를 시작합니다.
     * 
     * @param event 잔고 차감 완료 이벤트
     */
    void handle(BalanceDeducted event);

    /**
     * 재고 차감 완료 이벤트 처리
     * 재고가 성공적으로 차감되면 주문을 완료 처리합니다.
     * 
     * @param event 재고 차감 완료 이벤트
     */
    void handle(StockDeducted event);

    /**
     * 쿠폰 사용 실패 이벤트 처리
     * 쿠폰 사용이 실패하면 주문을 실패 처리합니다.
     * 
     * @param event 쿠폰 사용 실패 이벤트
     */
    void handle(CouponUseFailed event);

    /**
     * 잔고 차감 실패 이벤트 처리
     * 잔고 차감이 실패하면 쿠폰 사용을 취소합니다.
     * 
     * @param event 잔고 차감 실패 이벤트
     */
    void handle(BalanceDeductionFailed event);

    /**
     * 재고 차감 실패 이벤트 처리
     * 재고 차감이 실패하면 잔고 차감을 취소합니다.
     * 
     * @param event 재고 차감 실패 이벤트
     */
    void handle(StockDeductionFailed event);

    /**
     * 잔고 차감 취소 완료 이벤트 처리
     * 잔고 차감이 취소되면 쿠폰 사용을 취소합니다.
     * 
     * @param event 잔고 차감 취소 완료 이벤트
     */
    void handle(BalanceDeductionCancelled event);

    /**
     * 쿠폰 사용 취소 완료 이벤트 처리
     * 쿠폰 사용이 취소되면 주문을 실패 처리합니다.
     * 
     * @param event 쿠폰 사용 취소 완료 이벤트
     */
    void handle(CouponUseCancelled event);
} 