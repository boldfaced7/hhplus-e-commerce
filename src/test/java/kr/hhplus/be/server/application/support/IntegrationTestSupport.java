package kr.hhplus.be.server.application.support;

import com.redis.testcontainers.RedisContainer;
import kr.hhplus.be.server.adapter.out.cache.context.RedisPlaceOrderSagaContextRepository;
import kr.hhplus.be.server.adapter.out.persistence.balance.BalanceJpaRepository;
import kr.hhplus.be.server.adapter.out.persistence.balance.BalancePersistenceAdapter;
import kr.hhplus.be.server.adapter.out.persistence.coupon.CouponJpaRepository;
import kr.hhplus.be.server.adapter.out.persistence.coupon.CouponPersistenceAdapter;
import kr.hhplus.be.server.adapter.out.persistence.order.OrderJpaRepository;
import kr.hhplus.be.server.adapter.out.persistence.order.OrderPersistenceAdapter;
import kr.hhplus.be.server.adapter.out.persistence.orderitem.OrderItemJpaRepository;
import kr.hhplus.be.server.adapter.out.persistence.product.ProductJpaRepository;
import kr.hhplus.be.server.adapter.out.persistence.product.ProductPersistenceAdapter;
import org.junit.jupiter.api.Disabled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest
@Testcontainers
@Disabled
public abstract class IntegrationTestSupport {

    @Autowired public OrderPersistenceAdapter orderPersistenceAdapter;
    @Autowired public BalancePersistenceAdapter balancePersistenceAdapter;
    @Autowired public CouponPersistenceAdapter couponPersistenceAdapter;
    @Autowired public ProductPersistenceAdapter productPersistenceAdapter;

    @Autowired public BalanceJpaRepository balanceJpaRepository;
    @Autowired public CouponJpaRepository couponJpaRepository;
    @Autowired public OrderJpaRepository orderJpaRepository;
    @Autowired public OrderItemJpaRepository orderItemJpaRepository;
    @Autowired public ProductJpaRepository productJpaRepository;
    @Autowired public RedisPlaceOrderSagaContextRepository redisPlaceOrderSagaContextRepository;
    @Autowired public StringRedisTemplate redisTemplate;

    public void clearRepository() {
        balanceJpaRepository.deleteAll();
        couponJpaRepository.deleteAll();
        orderJpaRepository.deleteAll();
        orderItemJpaRepository.deleteAll();
        productJpaRepository.deleteAll();
        redisPlaceOrderSagaContextRepository.deleteAll();
        redisTemplate.delete(redisTemplate.keys("*"));
    }



    @Container
    private static final MySQLContainer<?> mySQLContainer = new MySQLContainer<>(
            "mysql:8.0.33"
    );

    @Container
    public static final RedisContainer redis = new RedisContainer(
            DockerImageName.parse("redis:7.2-alpine")
    );

    @DynamicPropertySource
    public static void setUpProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mySQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mySQLContainer::getUsername);
        registry.add("spring.datasource.password", mySQLContainer::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create");

        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", () -> redis.getMappedPort(RedisContainer.REDIS_PORT));
    }
} 