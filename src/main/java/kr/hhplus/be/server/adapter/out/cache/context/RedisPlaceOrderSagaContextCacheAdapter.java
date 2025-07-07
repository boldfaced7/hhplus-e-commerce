package kr.hhplus.be.server.adapter.out.cache.context;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import kr.hhplus.be.server.application.port.out.context.LoadPlaceOrderSagaContextPort;
import kr.hhplus.be.server.application.port.out.context.SavePlaceOrderSagaContextPort;
import kr.hhplus.be.server.application.service.saga.PlaceOrderSagaContext;
import kr.hhplus.be.server.domain.vo.order.Guid;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisPlaceOrderSagaContextCacheAdapter implements
        LoadPlaceOrderSagaContextPort,
        SavePlaceOrderSagaContextPort
{
    private final RedisPlaceOrderSagaContextRepository repository;

    @Override
    public Optional<PlaceOrderSagaContext> loadByGuid(Guid guid) {
        return repository.findById(guid.value())
                .map(RedisPlaceOrderSagaContextMapper::toDomain);
    }

    @Override
    public void save(PlaceOrderSagaContext context) {
        log.info("context saved: {}", context);
        repository.save(RedisPlaceOrderSagaContextMapper.toRedis(context));
    }
}
