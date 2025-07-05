package kr.hhplus.be.server.application.service;

import kr.hhplus.be.server.application.port.in.ChargeBalanceCommand;
import kr.hhplus.be.server.application.port.in.ChargeBalanceUseCase;
import kr.hhplus.be.server.application.port.out.balance.LoadBalancePort;
import kr.hhplus.be.server.application.port.out.balance.UpdateBalancePort;
import kr.hhplus.be.server.domain.exception.BalanceNotFoundException;
import kr.hhplus.be.server.domain.model.Balance;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChargeBalanceService implements ChargeBalanceUseCase {

    private final LoadBalancePort loadBalancePort;
    private final UpdateBalancePort updateBalancePort;

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public Balance chargeBalance(ChargeBalanceCommand command) {
        var loaded = loadBalancePort.loadBalanceForUpdate(command.userId())
                .orElseThrow(BalanceNotFoundException::new); // 잔액 조회(존재하지 않으면 예외 발생)

        var charged = loaded.charge(command.amount()); // 잔액 충전(Balance.charge() 사용)
        return updateBalancePort.updateBalance(charged); // 잔액 저장
    }

}
