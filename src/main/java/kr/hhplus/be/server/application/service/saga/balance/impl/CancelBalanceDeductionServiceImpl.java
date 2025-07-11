package kr.hhplus.be.server.application.service.saga.balance.impl;

import kr.hhplus.be.server.application.port.aop.DistributedLock;
import kr.hhplus.be.server.application.port.out.balance.LoadBalancePort;
import kr.hhplus.be.server.application.port.out.balance.UpdateBalancePort;
import kr.hhplus.be.server.application.port.out.event.PublishEventPort;
import kr.hhplus.be.server.application.service.saga.balance.CancelBalanceDeductionCommand;
import kr.hhplus.be.server.application.service.saga.balance.CancelBalanceDeductionService;
import kr.hhplus.be.server.domain.exception.BalanceNotFoundException;
import kr.hhplus.be.server.domain.model.Balance;
import kr.hhplus.be.server.domain.vo.order.OrderDiscountTotalPrice;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CancelBalanceDeductionServiceImpl implements CancelBalanceDeductionService {

    private final PublishEventPort publishEventPort;
    private final LoadBalancePort loadBalancePort;
    private final UpdateBalancePort updateBalancePort;

    @Override
    @DistributedLock(prefix = "balance:lock:", key = "#command.userId")
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void cancelBalanceDeduction(CancelBalanceDeductionCommand command) {
        var loaded = loadBalancePort.loadBalanceForUpdate(command.userId())
                .orElseThrow(BalanceNotFoundException::new);

        cancelBalanceDeduction(loaded, command.uuid(), command.price());
    }

    private void cancelBalanceDeduction(Balance balance, UUID uuid, OrderDiscountTotalPrice price) {
        balance.restore(uuid, price); // 잔액 차감 취소
        updateBalancePort.updateBalance(balance); // 잔액 업데이트
        publishEventPort.publishEvents(balance); // 이벤트 발행
    }
}
