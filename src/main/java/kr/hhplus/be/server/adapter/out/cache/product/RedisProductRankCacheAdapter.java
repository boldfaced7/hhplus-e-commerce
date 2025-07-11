package kr.hhplus.be.server.adapter.out.cache.product;

import kr.hhplus.be.server.application.port.out.product.AddProductSalesVolumePort;
import kr.hhplus.be.server.application.port.out.product.ListBestSellingProductIdPort;
import kr.hhplus.be.server.domain.vo.orderitem.OrderItemQuantities;
import kr.hhplus.be.server.domain.vo.product.ProductId;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class RedisProductRankCacheAdapter implements
        AddProductSalesVolumePort,
        ListBestSellingProductIdPort
{
    private final StringRedisTemplate redisTemplate;

    @Override
    public void addProductSalesVolume(OrderItemQuantities quantities, LocalDateTime soldAt) {
        String key = ProductRankingKeys.keyOf(soldAt);
        
        quantities.forEach((id, quantity) ->
                redisTemplate.opsForZSet().incrementScore(key, id.value().toString(), quantity.value())
        );
        redisTemplate.expire(key, ProductRankingKeys.TTL);
    }
    
    @Override
    public Page<ProductId> listBestSellingProductId(Pageable pageable) {
        var key = ProductRankingKeys.keyForNow();
        var productIds = listProductIds(key, pageable);
        long totalCount = getTotalCount(key);

        return new PageImpl<>(productIds, pageable, totalCount);
    }

    private List<ProductId> listProductIds(String key, Pageable pageable) {
        long start = pageable.getOffset();
        long end = start + pageable.getPageSize() - 1;

        Set<String> rawIds = redisTemplate.opsForZSet().reverseRange(key, start, end);
        if (rawIds == null || rawIds.isEmpty()) {
            return List.of();
        }

        return rawIds.stream()
                .map(Long::parseLong) // 예외 가능성 있음
                .map(ProductId::new)
                .toList();
    }

    private long getTotalCount(String key) {
        Long count = redisTemplate.opsForZSet().zCard(key);
        return (count != null) ? count : 0L;
    }
}
