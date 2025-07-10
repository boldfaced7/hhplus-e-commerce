package kr.hhplus.be.server.application.service.saga.product;

import kr.hhplus.be.server.domain.vo.orderitem.OrderItemQuantities;
import kr.hhplus.be.server.domain.vo.product.ProductId;

import java.util.Collection;
import java.util.UUID;

public record CancelStockDeductionCommand(
        UUID uuid,
        OrderItemQuantities quantities
) {
    public Collection<ProductId> productIds() {
        return quantities.productIds();
    }
}
