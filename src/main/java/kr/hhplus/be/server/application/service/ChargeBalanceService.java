package kr.hhplus.be.server.application.service;

import kr.hhplus.be.server.application.port.in.ChargeBalanceCommand;
import kr.hhplus.be.server.application.port.in.ChargeBalanceUseCase;
import kr.hhplus.be.server.application.port.out.balance.LoadBalancePort;
import kr.hhplus.be.server.application.port.out.balance.UpdateBalancePort;
import kr.hhplus.be.server.domain.exception.BalanceNotFoundException;
import kr.hhplus.be.server.domain.model.Balance;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChargeBalanceService implements ChargeBalanceUseCase {

    private final LoadBalancePort loadBalancePort;
    private final UpdateBalancePort updateBalancePort;

    @Override
    public Balance chargeBalance(ChargeBalanceCommand command) {
        // 잔액 조회(존재하지 않으면 예외 발생)
        Balance balance = loadBalancePort.loadBalance(command.userId())
                .orElseThrow(BalanceNotFoundException::new);

        // 잔액 충전(Balance.charge() 사용)
        balance.charge(command.amount());

        // 잔액 저장
        return updateBalancePort.updateBalance(balance);
    }
}
