package kr.hhplus.be.server.application.service.saga.product.impl;

import kr.hhplus.be.server.application.port.aop.DistributedMultiLock;
import kr.hhplus.be.server.application.port.out.event.PublishEventPort;
import kr.hhplus.be.server.application.port.out.product.LoadProductsPort;
import kr.hhplus.be.server.application.port.out.product.UpdateProductsPort;
import kr.hhplus.be.server.application.service.saga.product.CancelStockDeductionCommand;
import kr.hhplus.be.server.application.service.saga.product.CancelStockDeductionService;
import kr.hhplus.be.server.domain.model.Products;
import kr.hhplus.be.server.domain.vo.order.Guid;
import kr.hhplus.be.server.domain.vo.orderitem.OrderItemQuantities;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CancelStockDeductionServiceImpl implements CancelStockDeductionService {

    private final PublishEventPort publishEventPort;
    private final LoadProductsPort loadProductsPort;
    private final UpdateProductsPort updateProductsPort;

    @Override
    @DistributedMultiLock(prefix = "product:lock:", keys = "#command.productIds()")
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void cancelStockDeduction(CancelStockDeductionCommand command) {
        Products loaded = loadProductsPort.loadProductsForUpdate(command.productIds());
        cancelStockDeduction(loaded, command.guid(), command.quantities());
    }

    private void cancelStockDeduction(Products products, Guid guid, OrderItemQuantities quantities) {
        products.restoreAll(guid, quantities); // 모든 제품 재고 차감 취소
        updateProductsPort.updateProducts(products); // 제품 업데이트
        publishEventPort.publishEvents(products); // 이벤트 발행
    }



}
