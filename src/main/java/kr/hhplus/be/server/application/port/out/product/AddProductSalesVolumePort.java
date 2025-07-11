package kr.hhplus.be.server.application.port.out.product;

import kr.hhplus.be.server.domain.vo.orderitem.OrderItemQuantities;

import java.time.LocalDateTime;

public interface AddProductSalesVolumePort {
    void addProductSalesVolume(OrderItemQuantities quantities, LocalDateTime soldAt);
}
