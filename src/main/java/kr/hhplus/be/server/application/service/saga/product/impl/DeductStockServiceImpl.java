package kr.hhplus.be.server.application.service.saga.product.impl;

import kr.hhplus.be.server.application.port.aop.DistributedMultiLock;
import kr.hhplus.be.server.application.port.out.event.PublishEventPort;
import kr.hhplus.be.server.application.port.out.product.LoadProductsPort;
import kr.hhplus.be.server.application.port.out.product.UpdateProductsPort;
import kr.hhplus.be.server.application.service.saga.product.DeductStockCommand;
import kr.hhplus.be.server.application.service.saga.product.DeductStockService;
import kr.hhplus.be.server.domain.model.Products;
import kr.hhplus.be.server.domain.vo.orderitem.OrderItemQuantities;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeductStockServiceImpl implements DeductStockService {

    private final PublishEventPort publishEventPort;
    private final LoadProductsPort loadProductsPort;
    private final UpdateProductsPort updateProductsPort;

    @Override
    @DistributedMultiLock(prefix = "product:lock:", keys = "#command.productIds()")
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void deductStock(DeductStockCommand command) {
        var loaded = loadProductsPort.loadProductsForUpdate(command.productIds());
        deductStock(loaded, command.uuid(), command.quantities());
    }

    private void deductStock(Products products, UUID uuid, OrderItemQuantities quantities) {
        try {
            products.deductAll(uuid, quantities); // 재고 차감, 실패(재고 부족, 제품 없음) 시 예외 발생
            updateProductsPort.updateProducts(products); // 제품 업데이트

        } finally {
            publishEventPort.publishEvents(products); // 이벤트 발행
        }
    }

}
