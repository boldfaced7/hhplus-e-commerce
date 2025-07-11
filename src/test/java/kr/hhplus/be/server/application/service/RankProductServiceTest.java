package kr.hhplus.be.server.application.service;

import kr.hhplus.be.server.application.port.in.RankProductCommand;
import kr.hhplus.be.server.application.port.out.product.AddProductSalesVolumePort;
import kr.hhplus.be.server.domain.vo.product.ProductId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static kr.hhplus.be.server.application.service.TestConstants.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class RankProductServiceTest {

    @InjectMocks RankProductService rankProductService;
    @Mock AddProductSalesVolumePort addProductSalesVolumePort;

    final LocalDateTime soldAt = LocalDateTime.now();
    final RankProductCommand command = new RankProductCommand(ORDER_ITEM_QUANTITIES, soldAt);

    @Test
    @DisplayName("상품 판매량 등록에 성공한다")
    void rankProductSuccess() {
        // when
        rankProductService.rankProduct(command);

        // then
        verify(addProductSalesVolumePort).addProductSalesVolume(ORDER_ITEM_QUANTITIES, soldAt);
        verifyNoMoreInteractions(addProductSalesVolumePort);
    }

    @Test
    @DisplayName("다른 상품과 수량으로 판매량 등록에 성공한다")
    void rankProductWithDifferentQuantities() {
        // given
        var differentQuantities = orderItemQuantities(new ProductId(2L));
        var differentCommand = new RankProductCommand(differentQuantities, soldAt);

        // when
        rankProductService.rankProduct(differentCommand);

        // then
        verify(addProductSalesVolumePort).addProductSalesVolume(differentQuantities, soldAt);
        verifyNoMoreInteractions(addProductSalesVolumePort);
    }
} 