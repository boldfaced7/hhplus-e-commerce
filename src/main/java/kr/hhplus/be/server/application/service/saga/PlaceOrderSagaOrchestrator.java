package kr.hhplus.be.server.application.service.saga;

import kr.hhplus.be.server.application.port.in.OrchestratePlaceOrderSagaUseCase;
import kr.hhplus.be.server.application.port.out.context.LoadPlaceOrderSagaContextPort;
import kr.hhplus.be.server.application.port.out.context.SavePlaceOrderSagaContextPort;
import kr.hhplus.be.server.application.service.saga.balance.CancelBalanceDeductionCommand;
import kr.hhplus.be.server.application.service.saga.balance.CancelBalanceDeductionService;
import kr.hhplus.be.server.application.service.saga.balance.DeductBalanceCommand;
import kr.hhplus.be.server.application.service.saga.balance.DeductBalanceService;
import kr.hhplus.be.server.application.service.saga.coupon.CancelCouponUseCommand;
import kr.hhplus.be.server.application.service.saga.coupon.CancelCouponUseService;
import kr.hhplus.be.server.application.service.saga.coupon.UseCouponCommand;
import kr.hhplus.be.server.application.service.saga.coupon.UseCouponService;
import kr.hhplus.be.server.application.service.saga.order.CompleteOrderCommand;
import kr.hhplus.be.server.application.service.saga.order.CompleteOrderService;
import kr.hhplus.be.server.application.service.saga.product.DeductStockCommand;
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
import kr.hhplus.be.server.domain.vo.order.Guid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.function.Function;

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

    private final SavePlaceOrderSagaContextPort savePlaceOrderSagaContextPort;
    private final LoadPlaceOrderSagaContextPort loadPlaceOrderSagaContextPort;

    public void saveContext(OrderCreated event) {
        var context = PlaceOrderSagaContext.create(
                event.guid(),
                event.orderId(),
                event.userId(),
                event.couponId(),
                event.price(),
                event.quantities()
        );
        savePlaceOrderSagaContextPort.save(context);
    }

    // 주문 생성됨: 쿠폰 사용 서비스 호출
    public void handle(OrderCreated event) {
        var command = new UseCouponCommand(
                event.guid(),
                event.couponId()
        );
        useCouponService.useCoupon(command);
    }

    // 쿠폰 사용됨: 잔액 차감 서비스 호출
    public void handle(CouponUsed event) {
        var command = getCommand(event.guid(), context -> new DeductBalanceCommand(
            context.getGuid(),
            context.getUserId(),
            context.getPrice()
        ));
        deductBalanceService.deductBalance(command);
    }

    // 잔액 차감됨: 재고 차감 서비스 호출
    public void handle(BalanceDeducted event) {
        var command = getCommand(event.guid(), context -> new DeductStockCommand(
            context.getGuid(),
            context.getQuantities()
        ));
        deductStockService.deductStock(command);
    }

    // 재고 차감됨: 주문 종결 서비스 호출
    public void handle(StockDeducted event) {
        var command = getCommand(event.guid(), context -> new CompleteOrderCommand(
            context.getGuid(),
            context.getOrderId()
        ));
        completeOrderService.succeedOrder(command);
    }

    // 쿠폰 사용 실패: 주문 종결 서비스 호출
    public void handle(CouponUseFailed event) {
        var command = getCommand(event.guid(), context -> new CompleteOrderCommand(
            context.getGuid(),
            context.getOrderId()
        ));
        completeOrderService.failOrder(command);
    }

    // 잔액 차감 실패: 쿠폰 사용 취소 서비스 호출
    public void handle(BalanceDeductionFailed event) {
        var command = getCommand(event.guid(), context -> new CancelCouponUseCommand(
            context.getGuid(),
            context.getCouponId()
        ));
        cancelCouponUseService.cancelCouponUse(command);
    }

    // 재고 차감 실패: 잔액 차감 취소 서비스 호출
    public void handle(StockDeductionFailed event) {
        var command = getCommand(event.guid(), context -> new CancelBalanceDeductionCommand(
            context.getGuid(),
            context.getUserId(),
            context.getPrice()
        ));
        cancelBalanceDeductionService.cancelBalanceDeduction(command);
    }

    // 잔액 차감 취소됨: 쿠폰 사용 취소 서비스 호출
    public void handle(BalanceDeductionCancelled event) {
        var command = getCommand(event.guid(), context -> new CancelCouponUseCommand(
            context.getGuid(),
            context.getCouponId()
        ));
        cancelCouponUseService.cancelCouponUse(command);
    }

    // 쿠폰 사용 취소됨: 주문 종결 서비스 호출
    public void handle(CouponUseCancelled event) {
        var command = getCommand(event.guid(), context -> new CompleteOrderCommand(
            context.getGuid(),
            context.getOrderId()
        ));
        completeOrderService.failOrder(command);
    }

    private <T> T getCommand(Guid guid, Function<PlaceOrderSagaContext, T> mapper) {
        return loadPlaceOrderSagaContextPort.loadByGuid(guid)
                .map(mapper)
                .orElseThrow(() -> new IllegalStateException("PlaceOrderSagaContext not found"));
    }


}
