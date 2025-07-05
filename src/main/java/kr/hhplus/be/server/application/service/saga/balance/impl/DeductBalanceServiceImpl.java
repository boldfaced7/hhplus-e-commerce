package kr.hhplus.be.server.application.service.saga.balance.impl;

import kr.hhplus.be.server.application.port.out.balance.LoadBalancePort;
import kr.hhplus.be.server.application.port.out.balance.UpdateBalancePort;
import kr.hhplus.be.server.application.port.out.event.PublishEventPort;
import kr.hhplus.be.server.application.service.saga.balance.DeductBalanceService;
import kr.hhplus.be.server.domain.event.DomainEvent;
import kr.hhplus.be.server.domain.model.Balance;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeductBalanceServiceImpl implements DeductBalanceService {

    private final PublishEventPort publishEventPort;
    private final LoadBalancePort loadBalancePort;
    private final UpdateBalancePort updateBalancePort;

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void deductBalance(DomainEvent event) {
        loadBalancePort.loadBalanceForUpdate(event.userId())
                .orElse(Balance.unavailable()) // 조회 결과가 없는 경우, 사용 불가 잔액 사용
                .apply(balance -> deductBalance(balance, event));
    }

    private void deductBalance(Balance balance, DomainEvent event) {
        try {
            balance.deduct(event.orderDiscountTotalPrice()); // 잔액 차감, 실패 시 예외 발생
            balance.markBalanceDeducted(event); // 잔액 차감 성공 이벤트 생성
            updateBalancePort.updateBalance(balance); // 잔액 업데이트

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            balance.markBalanceDeductionFailed(event); // 잔액 차감 실패 이벤트 생성
        }
        publishEventPort.publishEvents(balance); // 이벤트 발행
    }

}
