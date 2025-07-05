package kr.hhplus.be.server.domain.model;

import kr.hhplus.be.server.domain.event.DomainEvent;
import kr.hhplus.be.server.domain.event.product.StockDeducted;
import kr.hhplus.be.server.domain.event.product.StockDeductionCancelled;
import kr.hhplus.be.server.domain.event.product.StockDeductionFailed;
import kr.hhplus.be.server.domain.exception.ProductNotFoundException;
import kr.hhplus.be.server.domain.vo.orderitem.OrderItemQuantity;
import kr.hhplus.be.server.domain.vo.product.ProductId;
import kr.hhplus.be.server.domain.vo.product.ProductPrice;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@ToString
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Products extends EventDomain {
    private Map<ProductId, Product> products;

    public static Products create(Map<ProductId, Product> products) {
        return new Products(products);
    }

    public Map<ProductId, ProductPrice> getProductPrices() {
        return products.values().stream()
                .collect(Collectors.toMap(
                        Product::getProductId,
                        Product::getPrice
                ));
    }

    public Product getProduct(ProductId productId) {
        return products.get(productId);
    }

    public Products addAll(Map<ProductId, OrderItemQuantity> orderItemQuantities) {
        checkAllProductExistence();
        orderItemQuantities.forEach((productId, orderItemQuantity) -> {
            products.get(productId).add(orderItemQuantity);
        });
        return this;
    }

    public Products deductAll(Map<ProductId, OrderItemQuantity> orderItemQuantities) {
        checkAllProductExistence();
        orderItemQuantities.forEach((productId, orderItemQuantity) -> {
            products.get(productId).deduct(orderItemQuantity);
        });
        return this;
    }

    public void checkAllProductExistence() {
        if (!hasAllProducts()) {
            throw new ProductNotFoundException();
        }
    }

    public boolean hasAllProducts() {
        if (products == null || products.isEmpty()) return false;

        return products.values().stream()
                .allMatch(Objects::nonNull);
    }

    public void apply(Consumer<Products> consumer) {
        if (hasAllProducts()) {
            consumer.accept(this);
        }
    }

    public void markStockDeducted(DomainEvent event) {
        addEvent(new StockDeducted(event));
    }

    public void markStockDeductionFailed(DomainEvent event) {
        addEvent(new StockDeductionFailed(event));
    }

    public void markStockDeductionCancelled(DomainEvent event) {
        addEvent(new StockDeductionCancelled(event));
    }

}
