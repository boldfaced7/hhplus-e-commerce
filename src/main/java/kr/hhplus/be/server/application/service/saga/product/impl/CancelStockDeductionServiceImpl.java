package kr.hhplus.be.server.application.service.saga.product.impl;

import kr.hhplus.be.server.application.port.out.event.PublishEventPort;
import kr.hhplus.be.server.application.port.out.product.LoadProductsPort;
import kr.hhplus.be.server.application.port.out.product.UpdateProductsPort;
import kr.hhplus.be.server.application.service.saga.product.CancelStockDeductionService;
import kr.hhplus.be.server.domain.event.DomainEvent;
import kr.hhplus.be.server.domain.model.Products;
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
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void cancelStockDeduction(DomainEvent event) {
        loadProductsPort.loadProductsForUpdate(event.productIds())
                .apply(products -> cancelStockDeduction(products, event));
    }

    private void cancelStockDeduction(Products products, DomainEvent event) {
        var cancelled = products.addAll(event.orderItemQuantities()); // 모든 제품 재고 차감 취소
        cancelled.markStockDeductionCancelled(event);                 // 재고 차감됨 이벤트 생성

        updateProductsPort.updateProducts(cancelled);                 // 제품 업데이트
        publishEventPort.publishEvents(cancelled);                    // 이벤트 발행
    }

}
