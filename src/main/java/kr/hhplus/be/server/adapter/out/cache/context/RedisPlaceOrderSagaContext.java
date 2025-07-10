package kr.hhplus.be.server.adapter.out.cache.context;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.util.Map;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@RedisHash(value = "place-order-saga-context", timeToLive = 3600)
public class RedisPlaceOrderSagaContext {

    @Id
    private String uuid;
    private Long orderId;
    private Long userId;
    private Long couponId;
    private Long price;
    private Map<Long, Integer> quantities;

}
