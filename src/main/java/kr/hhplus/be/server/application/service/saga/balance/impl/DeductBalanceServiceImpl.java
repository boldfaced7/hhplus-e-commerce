package kr.hhplus.be.server.application.service.saga.balance.impl;

import kr.hhplus.be.server.application.port.aop.DistributedLock;
import kr.hhplus.be.server.application.port.out.balance.LoadBalancePort;
import kr.hhplus.be.server.application.port.out.balance.UpdateBalancePort;
import kr.hhplus.be.server.application.port.out.event.PublishEventPort;
import kr.hhplus.be.server.application.service.saga.balance.DeductBalanceCommand;
import kr.hhplus.be.server.application.service.saga.balance.DeductBalanceService;
import kr.hhplus.be.server.domain.model.Balance;
import kr.hhplus.be.server.domain.vo.order.OrderDiscountTotalPrice;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeductBalanceServiceImpl implements DeductBalanceService {

    private final PublishEventPort publishEventPort;
    private final LoadBalancePort loadBalancePort;
    private final UpdateBalancePort updateBalancePort;

    @Override
    @DistributedLock(prefix = "balance:lock:", key = "#command.userId")
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void deductBalance(DeductBalanceCommand command) {
        var loaded = loadBalancePort.loadBalanceForUpdate(command.userId())
                .orElse(Balance.unavailable()); // 조회 결과가 없는 경우, 사용 불가 잔액 사용

        deductBalance(loaded, command.uuid(), command.price());
    }

    private void deductBalance(Balance balance, UUID uuid, OrderDiscountTotalPrice price) {
        try {
            balance.deduct(uuid, price); // 잔액 차감, 실패 시 예외 발생
            updateBalancePort.updateBalance(balance); // 잔액 업데이트

        } finally {
            publishEventPort.publishEvents(balance); // 이벤트 발행
        }
    }

}
