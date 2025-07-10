package kr.hhplus.be.server.domain.vo.product;

import kr.hhplus.be.server.domain.vo.order.OrderOriginalTotalPrice;
import kr.hhplus.be.server.domain.vo.orderitem.OrderItemQuantities;

import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

public record ProductPrices(
        Map<ProductId, ProductPrice> productPriceMap
) {
    public ProductPrice getPrice(ProductId productId) {
        return productPriceMap.get(productId);
    }

    public Stream<Map.Entry<ProductId, ProductPrice>> stream() {
        return productPriceMap.entrySet().stream();
    }

    public Set<ProductId> keySet() {
        return productPriceMap.keySet();
    }

    public OrderOriginalTotalPrice calculateOriginalPrice(
            OrderItemQuantities orderItemQuantities
    ) {
        Long price = productPriceMap.keySet().stream()
                .map(id -> orderItemQuantities.getQuantity(id).value() * getPrice(id).value())
                .reduce(0L, Long::sum);

        return new OrderOriginalTotalPrice(price);
    }

}
