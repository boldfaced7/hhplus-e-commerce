package kr.hhplus.be.server.application.port.out.balance;

import kr.hhplus.be.server.domain.model.Balance;
import kr.hhplus.be.server.domain.vo.user.UserId;

import java.util.Optional;

public interface LoadBalancePort {
    Optional<Balance> loadBalance(UserId userId);
    Optional<Balance> loadBalanceForUpdate(UserId userId);

}
