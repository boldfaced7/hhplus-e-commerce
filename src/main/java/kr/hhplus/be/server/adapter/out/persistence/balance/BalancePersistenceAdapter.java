package kr.hhplus.be.server.adapter.out.persistence.balance;

import kr.hhplus.be.server.application.port.out.balance.LoadBalancePort;
import kr.hhplus.be.server.application.port.out.balance.SaveBalancePort;
import kr.hhplus.be.server.application.port.out.balance.UpdateBalancePort;
import kr.hhplus.be.server.domain.model.Balance;
import kr.hhplus.be.server.domain.vo.user.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class BalancePersistenceAdapter implements 
        LoadBalancePort, 
        SaveBalancePort, 
        UpdateBalancePort
{
    private final BalanceJpaRepository balanceJpaRepository;

    @Override
    public Optional<Balance> loadBalance(UserId userId) {
        return balanceJpaRepository.findByUserId(userId.value())
                .map(BalanceMapper::toDomain);
    }

    @Override
    public Optional<Balance> loadBalanceForUpdate(UserId userId) {
        return balanceJpaRepository.findByUserIdForUpdate(userId.value())
                .map(BalanceMapper::toDomain);
    }

    @Override
    public Balance saveBalance(Balance balance) {
        var saved = balanceJpaRepository.save(BalanceMapper.toJpa(balance));
        return BalanceMapper.toDomain(saved);
    }

    @Override
    public Balance updateBalance(Balance balance) {
        var updated = balanceJpaRepository.save(BalanceMapper.toJpa(balance));
        return BalanceMapper.toDomain(updated);
    }
}
