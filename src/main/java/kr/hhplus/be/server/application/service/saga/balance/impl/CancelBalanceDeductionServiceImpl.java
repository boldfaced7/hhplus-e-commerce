package kr.hhplus.be.server.application.service.saga.balance.impl;

import kr.hhplus.be.server.application.port.out.balance.LoadBalancePort;
import kr.hhplus.be.server.application.port.out.balance.UpdateBalancePort;
import kr.hhplus.be.server.application.port.out.event.PublishEventPort;
import kr.hhplus.be.server.application.service.saga.balance.CancelBalanceDeductionService;
import kr.hhplus.be.server.domain.event.DomainEvent;
import kr.hhplus.be.server.domain.model.Balance;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CancelBalanceDeductionServiceImpl implements CancelBalanceDeductionService {

    private final PublishEventPort publishEventPort;
    private final LoadBalancePort loadBalancePort;
    private final UpdateBalancePort updateBalancePort;

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void cancelBalanceDeduction(DomainEvent event) {
        loadBalancePort.loadBalanceForUpdate(event.userId())
                .ifPresent(balance -> cancelBalanceDeduction(balance, event));
    }

    private void cancelBalanceDeduction(Balance balance, DomainEvent event) {
        var cancelled = balance.charge(event.orderDiscountTotalPrice()); // 잔액 차감 취소
        cancelled.markBalanceDeductionCancelled(event); // 잔액 차감 취소됨 이벤트 생성

        updateBalancePort.updateBalance(cancelled); // 잔액 업데이트
        publishEventPort.publishEvents(cancelled); // 이벤트 발행
    }

}
