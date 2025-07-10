package kr.hhplus.be.server.domain.model;

import kr.hhplus.be.server.domain.exception.ProductUnavailableException;
import kr.hhplus.be.server.domain.vo.orderitem.OrderItemQuantity;
import kr.hhplus.be.server.domain.vo.product.ProductId;
import kr.hhplus.be.server.domain.vo.product.ProductName;
import kr.hhplus.be.server.domain.vo.product.ProductPrice;
import kr.hhplus.be.server.domain.vo.product.ProductStock;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@ToString
@Getter
@AllArgsConstructor
public class Product extends EventDomain {

    private ProductId productId;
    private ProductName name;
    private ProductPrice price;
    private ProductStock stock;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static Product unavailable() {
        return new Product(
                null,
                null,
                null,
                null,
                LocalDateTime.MAX,
                LocalDateTime.MAX
        );
    }

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

    public void add(OrderItemQuantity quantity) {
        checkProductAvailable();
        this.stock = this.stock.add(quantity);
    }

    public void deduct(OrderItemQuantity quantity) {
        checkProductAvailable();
        this.stock = this.stock.subtract(quantity);
    }

    private void checkProductAvailable() {
        if (this.createdAt == LocalDateTime.MAX
                && this.updatedAt == LocalDateTime.MAX) {
            throw new ProductUnavailableException();
        }
    }

}
