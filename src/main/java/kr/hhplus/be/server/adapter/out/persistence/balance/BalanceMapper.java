package kr.hhplus.be.server.adapter.out.persistence.balance;

import kr.hhplus.be.server.domain.model.Balance;
import kr.hhplus.be.server.domain.vo.balance.BalanceAmount;
import kr.hhplus.be.server.domain.vo.balance.BalanceId;
import kr.hhplus.be.server.domain.vo.user.UserId;

public class BalanceMapper {

    public static BalanceJpa toJpa(Balance balance) {
        return new BalanceJpa(
            (balance.getBalanceId() != null) ? balance.getBalanceId().value() : null,
            balance.getUserId().value(),
            balance.getAmount().value(),
            balance.getCreatedAt(),
            balance.getUpdatedAt()
        );
    }

    public static Balance toDomain(BalanceJpa balanceJpa) {
        return Balance.create(
            new BalanceId(balanceJpa.getId()),
            new UserId(balanceJpa.getUserId()),
            new BalanceAmount(balanceJpa.getAmount()),
            balanceJpa.getCreatedAt(),
            balanceJpa.getUpdatedAt()
        );
    }
}
