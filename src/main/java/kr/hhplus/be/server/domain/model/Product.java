package kr.hhplus.be.server.domain.model;

import kr.hhplus.be.server.domain.vo.orderitem.OrderItemQuantity;
import kr.hhplus.be.server.domain.vo.product.ProductId;
import kr.hhplus.be.server.domain.vo.product.ProductName;
import kr.hhplus.be.server.domain.vo.product.ProductPrice;
import kr.hhplus.be.server.domain.vo.product.ProductStock;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Product {
    private ProductId productId;
    private ProductName name;
    private ProductPrice price;
    private ProductStock stock;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static Product create(
            ProductName name,
            ProductPrice price,
            ProductStock stock
    ) {
        return new Product(
                null,
                name,
                price,
                stock,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    public static Product create(
            ProductId productId,
            ProductName name,
            ProductPrice price,
            ProductStock stock,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        return new Product(
                productId,
                name,
                price,
                stock,
                createdAt,
                updatedAt
        );
    }

    public void decreaseStock(OrderItemQuantity quantity) {
        this.stock = this.stock.subtract(quantity);
    }
}
