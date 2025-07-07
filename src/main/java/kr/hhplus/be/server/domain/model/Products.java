package kr.hhplus.be.server.domain.model;

import kr.hhplus.be.server.domain.event.product.StockDeducted;
import kr.hhplus.be.server.domain.event.product.StockDeductionCancelled;
import kr.hhplus.be.server.domain.event.product.StockDeductionFailed;
import kr.hhplus.be.server.domain.exception.ProductNotFoundException;
import kr.hhplus.be.server.domain.vo.order.Guid;
import kr.hhplus.be.server.domain.vo.orderitem.OrderItemQuantities;
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

    public void restoreAll(Guid guid, OrderItemQuantities quantities) {
        checkAllProductExistence();
        quantities.forEach((productId, orderItemQuantity) -> {
            products.get(productId).add(orderItemQuantity);
        });
        markStockDeductionCancelled(guid, quantities);
    }

    public void deductAll(Guid guid, OrderItemQuantities quantities) {
        checkAllProductExistence();
        try {
            quantities.forEach((productId, orderItemQuantity) -> {
                products.get(productId).deduct(orderItemQuantity);
            });
            markStockDeducted(guid, quantities);
        } catch (Exception e) {
            markStockDeductionFailed(guid, quantities);
            throw e;
        }
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

    private void markStockDeducted(Guid guid, OrderItemQuantities quantities) {
        addEvent(new StockDeducted(guid, quantities));
    }

    private void markStockDeductionFailed(Guid guid, OrderItemQuantities quantities) {
        addEvent(new StockDeductionFailed(guid, quantities));
    }

    private void markStockDeductionCancelled(Guid guid, OrderItemQuantities quantities) {
        addEvent(new StockDeductionCancelled(guid, quantities));
    }

}
