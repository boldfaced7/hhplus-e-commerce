package kr.hhplus.be.server.application.service.saga.product.impl;

import kr.hhplus.be.server.application.port.out.event.PublishEventPort;
import kr.hhplus.be.server.application.port.out.product.LoadProductsPort;
import kr.hhplus.be.server.application.port.out.product.UpdateProductsPort;
import kr.hhplus.be.server.application.service.saga.product.DeductStockService;
import kr.hhplus.be.server.domain.event.DomainEvent;
import kr.hhplus.be.server.domain.model.Products;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeductStockServiceImpl implements DeductStockService {

    private final PublishEventPort publishEventPort;
    private final LoadProductsPort loadProductsPort;
    private final UpdateProductsPort updateProductsPort;

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void deductStock(DomainEvent event) {
        loadProductsPort.loadProductsForUpdate(event.productIds())
                .apply(products -> deductStock(products, event));
    }

    private void deductStock(Products products, DomainEvent event) {
        try {
            products.deductAll(event.orderItemQuantities()); // 재고 차감, 실패(재고 부족, 제품 없음) 시 예외 발생
            products.markStockDeducted(event); // 재고 차감 성공 이벤트 생성
            updateProductsPort.updateProducts(products); // 제품 업데이트

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            products.markStockDeductionFailed(event); // 재고 차감 실패 이벤트 생성
        }
        publishEventPort.publishEvents(products); // 이벤트 발행
    }

}
