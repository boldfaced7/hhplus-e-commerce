package kr.hhplus.be.server.adapter.out.cache.context;

import org.springframework.data.repository.CrudRepository;

public interface RedisPlaceOrderSagaContextRepository extends CrudRepository<RedisPlaceOrderSagaContext, String> {
}
