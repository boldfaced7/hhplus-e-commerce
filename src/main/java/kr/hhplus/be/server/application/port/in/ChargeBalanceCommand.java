package kr.hhplus.be.server.application.port.in;

import kr.hhplus.be.server.domain.vo.balance.BalanceAmount;
import kr.hhplus.be.server.domain.vo.user.UserId;

public record ChargeBalanceCommand(
        UserId userId,
        BalanceAmount amount
) {
}
