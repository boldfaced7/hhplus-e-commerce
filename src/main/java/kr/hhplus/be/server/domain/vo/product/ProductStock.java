package kr.hhplus.be.server.domain.vo.product;

import kr.hhplus.be.server.domain.exception.ProductInsufficientException;
import kr.hhplus.be.server.domain.vo.orderitem.OrderItemQuantity;

public record ProductStock(Long value) {

    public ProductStock subtract(OrderItemQuantity quantity) {
        if (this.value < quantity.value()) {
            throw new ProductInsufficientException();
        }
        return new ProductStock(this.value - quantity.value());
    }

    public ProductStock add(OrderItemQuantity quantity) {
        return new ProductStock(this.value + quantity.value());
    }
}
