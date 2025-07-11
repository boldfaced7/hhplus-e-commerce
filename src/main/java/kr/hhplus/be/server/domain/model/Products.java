package kr.hhplus.be.server.domain.model;

import kr.hhplus.be.server.domain.event.product.StockDeducted;
import kr.hhplus.be.server.domain.event.product.StockDeductionCancelled;
import kr.hhplus.be.server.domain.event.product.StockDeductionFailed;
import kr.hhplus.be.server.domain.exception.ProductNotFoundException;
import kr.hhplus.be.server.domain.vo.orderitem.OrderItemQuantities;
import kr.hhplus.be.server.domain.vo.orderitem.OrderItemQuantity;
import kr.hhplus.be.server.domain.vo.product.ProductId;
import kr.hhplus.be.server.domain.vo.product.ProductPrices;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@ToString
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Products extends EventDomain {

    private Map<ProductId, Product> productMap;

    public static Products create(Map<ProductId, Product> products) {
        return new Products(products);
    }

    public ProductPrices getProductPrices() {
        var collected = productMap.values().stream()
                .collect(Collectors.toMap(
                        Product::getProductId,
                        Product::getPrice
                ));
        return new ProductPrices(collected);
    }

    public Product getProduct(ProductId productId) {
        return productMap.get(productId);
    }

    public void restoreAll(UUID uuid, OrderItemQuantities quantities) {
        checkAllProductExistence();
        quantities.forEach(this::add);
        markStockDeductionCancelled(uuid, quantities);
    }

    public void deductAll(UUID uuid, OrderItemQuantities quantities) {
        checkAllProductExistence();
        try {
            quantities.forEach(this::deduct);
            markStockDeducted(uuid, quantities);
        } catch (Exception e) {
            markStockDeductionFailed(uuid, quantities);
            throw e;
        }
    }


    public void checkAllProductExistence() {
        if (!hasAllProducts()) {
            throw new ProductNotFoundException();
        }
    }

    public boolean hasAllProducts() {
        if (productMap == null || productMap.isEmpty()) return false;

        return productMap.values().stream()
                .allMatch(Objects::nonNull);
    }

    private void markStockDeducted(UUID uuid, OrderItemQuantities quantities) {
        addEvent(new StockDeducted(uuid, quantities));
    }

    private void markStockDeductionFailed(UUID uuid, OrderItemQuantities quantities) {
        addEvent(new StockDeductionFailed(uuid, quantities));
    }

    private void markStockDeductionCancelled(UUID uuid, OrderItemQuantities quantities) {
        addEvent(new StockDeductionCancelled(uuid, quantities));
    }

    private void add(ProductId productId, OrderItemQuantity orderItemQuantity) {
        productMap.get(productId).add(orderItemQuantity);
    }

    private void deduct(ProductId productId, OrderItemQuantity orderItemQuantity) {
        productMap.get(productId).deduct(orderItemQuantity);
    }
}
