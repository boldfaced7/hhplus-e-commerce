package kr.hhplus.be.server.domain.vo.orderitem;

import kr.hhplus.be.server.domain.vo.product.ProductId;

import java.util.Collection;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

public record OrderItemQuantities(
        Map<ProductId, OrderItemQuantity> quantities
) {
    public OrderItemQuantity getQuantity(ProductId productId) {
        return quantities.get(productId);
    }

    public Collection<ProductId> productIds() {
        return quantities.keySet();
    }

    public void forEach(BiConsumer<? super ProductId, ? super OrderItemQuantity> action) {
        quantities.forEach(action);
    }

    public Stream<Map.Entry<ProductId, OrderItemQuantity>> stream() {
        return quantities.entrySet().stream();
    }
}
