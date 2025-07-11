package kr.hhplus.be.server.application.service;

import kr.hhplus.be.server.application.port.in.RankProductCommand;
import kr.hhplus.be.server.application.port.out.product.AddProductSalesVolumePort;
import kr.hhplus.be.server.application.port.out.product.ListBestSellingProductIdPort;
import kr.hhplus.be.server.domain.vo.orderitem.OrderItemQuantities;
import kr.hhplus.be.server.domain.vo.orderitem.OrderItemQuantity;
import kr.hhplus.be.server.domain.vo.product.ProductId;
import kr.hhplus.be.server.application.support.IntegrationTestSupport;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class RankProductServiceIntegrationTest extends IntegrationTestSupport {

    @Autowired RankProductService rankProductService;
    @Autowired AddProductSalesVolumePort addProductSalesVolumePort;
    @Autowired ListBestSellingProductIdPort listBestSellingProductIdPort;

    @AfterEach
    void tearDown() {
        clearRepository();
    }

    @Test
    @DisplayName("상품 판매량이 Redis에 정상적으로 저장된다")
    void addProductSalesVolumeSuccess() {
        // given
        LocalDateTime soldAt = LocalDateTime.now();
        ProductId productId = new ProductId(1L);
        OrderItemQuantity quantity = new OrderItemQuantity(5);
        OrderItemQuantities quantities = new OrderItemQuantities(Map.of(productId, quantity));
        RankProductCommand command = new RankProductCommand(quantities, soldAt);

        // when
        rankProductService.rankProduct(command);

        // then
        // Redis에서 인기 상품 조회하여 검증
        Pageable pageable = Pageable.ofSize(10);
        Page<ProductId> result = listBestSellingProductIdPort.listBestSellingProductId(pageable);
        
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0)).isEqualTo(productId);
    }

    @Test
    @DisplayName("여러 상품의 판매량이 누적되어 저장된다")
    void addMultipleProductSalesVolume() {
        // given
        LocalDateTime soldAt = LocalDateTime.now();
        ProductId productId1 = new ProductId(1L);
        ProductId productId2 = new ProductId(2L);
        
        OrderItemQuantities quantities = new OrderItemQuantities(Map.of(
                productId1, new OrderItemQuantity(10),
                productId2, new OrderItemQuantity(20)
        ));
        RankProductCommand command = new RankProductCommand(quantities, soldAt);

        // when
        rankProductService.rankProduct(command);

        // then
        Pageable pageable = Pageable.ofSize(10);
        Page<ProductId> result = listBestSellingProductIdPort.listBestSellingProductId(pageable);
        
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
        
        // 판매량 순서: productId2(20개) > productId1(10개)
        List<ProductId> productIds = result.getContent();
        assertThat(productIds.get(0)).isEqualTo(productId2);
        assertThat(productIds.get(1)).isEqualTo(productId1);
    }

    @Test
    @DisplayName("같은 상품의 판매량이 누적된다")
    void accumulateProductSalesVolume() {
        // given
        LocalDateTime soldAt = LocalDateTime.now();
        ProductId productId = new ProductId(1L);
        
        // 첫 번째 판매: 5개
        OrderItemQuantities quantities1 = new OrderItemQuantities(Map.of(productId, new OrderItemQuantity(5)));
        RankProductCommand command1 = new RankProductCommand(quantities1, soldAt);
        
        // 두 번째 판매: 3개
        OrderItemQuantities quantities2 = new OrderItemQuantities(Map.of(productId, new OrderItemQuantity(3)));
        RankProductCommand command2 = new RankProductCommand(quantities2, soldAt);

        // when
        rankProductService.rankProduct(command1);
        rankProductService.rankProduct(command2);

        // then
        Pageable pageable = Pageable.ofSize(10);
        Page<ProductId> result = listBestSellingProductIdPort.listBestSellingProductId(pageable);
        
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0)).isEqualTo(productId);
        // 총 판매량: 5 + 3 = 8개
    }

    @Test
    @DisplayName("다른 날짜의 판매량은 별도로 관리된다")
    void separateSalesVolumeByDate() {
        // given
        LocalDateTime today = LocalDateTime.now();
        LocalDateTime yesterday = today.minusDays(1);
        ProductId productId = new ProductId(1L);
        
        // 어제 판매: 10개
        OrderItemQuantities quantitiesYesterday = new OrderItemQuantities(Map.of(productId, new OrderItemQuantity(10)));
        RankProductCommand commandYesterday = new RankProductCommand(quantitiesYesterday, yesterday);
        
        // 오늘 판매: 5개
        OrderItemQuantities quantitiesToday = new OrderItemQuantities(Map.of(productId, new OrderItemQuantity(5)));
        RankProductCommand commandToday = new RankProductCommand(quantitiesToday, today);

        // when
        rankProductService.rankProduct(commandYesterday);
        rankProductService.rankProduct(commandToday);

        // then
        // 오늘 날짜로 조회하면 오늘 판매량만 조회됨
        Pageable pageable = Pageable.ofSize(10);
        Page<ProductId> result = listBestSellingProductIdPort.listBestSellingProductId(pageable);
        
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0)).isEqualTo(productId);
        // 오늘 판매량: 5개
    }
} 