package kr.hhplus.be.server.application.service;

import kr.hhplus.be.server.application.port.in.ListBestSellingProductCommand;
import kr.hhplus.be.server.application.port.in.RankProductCommand;
import kr.hhplus.be.server.application.port.out.product.SaveProductPort;
import kr.hhplus.be.server.domain.model.Product;
import kr.hhplus.be.server.domain.vo.orderitem.OrderItemQuantities;
import kr.hhplus.be.server.domain.vo.orderitem.OrderItemQuantity;
import kr.hhplus.be.server.domain.vo.product.ProductId;
import kr.hhplus.be.server.domain.vo.product.ProductName;
import kr.hhplus.be.server.domain.vo.product.ProductPrice;
import kr.hhplus.be.server.domain.vo.product.ProductStock;
import kr.hhplus.be.server.application.support.IntegrationTestSupport;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ListBestSellingProductServiceIntegrationTest extends IntegrationTestSupport {

    @Autowired ListBestSellingProductService listBestSellingProductService;
    @Autowired RankProductService rankProductService;
    @Autowired SaveProductPort saveProductPort;

    Product product1, product2, product3;
    ProductId productId1, productId2, productId3;

    @BeforeEach
    void setUp() {
        // 테스트 상품 생성
        product1 = saveProductPort.saveProduct(Product.create(
                new ProductName("인기 상품 1"),
                new ProductPrice(10000L),
                new ProductStock(100L)
        ));
        product2 = saveProductPort.saveProduct(Product.create(
                new ProductName("인기 상품 2"),
                new ProductPrice(20000L),
                new ProductStock(200L)
        ));
        product3 = saveProductPort.saveProduct(Product.create(
                new ProductName("인기 상품 3"),
                new ProductPrice(30000L),
                new ProductStock(300L)
        ));

        productId1 = product1.getProductId();
        productId2 = product2.getProductId();
        productId3 = product3.getProductId();
    }

    @AfterEach
    void tearDown() {
        clearRepository();
    }

    @Test
    @DisplayName("판매량이 높은 순서대로 인기 상품이 조회된다")
    void listBestSellingProductsBySalesVolume() {
        // given
        LocalDateTime soldAt = LocalDateTime.now();
        
        // 상품1: 10개 판매
        rankProductService.rankProduct(new RankProductCommand(
                new OrderItemQuantities(Map.of(productId1, new OrderItemQuantity(10))),
                soldAt
        ));
        
        // 상품2: 20개 판매
        rankProductService.rankProduct(new RankProductCommand(
                new OrderItemQuantities(Map.of(productId2, new OrderItemQuantity(20))),
                soldAt
        ));
        
        // 상품3: 15개 판매
        rankProductService.rankProduct(new RankProductCommand(
                new OrderItemQuantities(Map.of(productId3, new OrderItemQuantity(15))),
                soldAt
        ));

        // when
        Pageable pageable = Pageable.ofSize(10);
        ListBestSellingProductCommand command = new ListBestSellingProductCommand(pageable);
        Page<Product> result = listBestSellingProductService.listBestSellingProductToady(command);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(3);
        
        // 판매량 순서: 상품2(20개) > 상품3(15개) > 상품1(10개)
        List<Product> products = result.getContent();
        assertThat(products.get(0).getProductId()).isEqualTo(productId2); // 20개
        assertThat(products.get(1).getProductId()).isEqualTo(productId3); // 15개
        assertThat(products.get(2).getProductId()).isEqualTo(productId1); // 10개
    }

    @Test
    @DisplayName("인기 상품이 없는 경우 빈 페이지를 반환한다")
    void listBestSellingProductsEmpty() {
        // given - 판매 기록 없음

        // when
        Pageable pageable = Pageable.ofSize(10);
        ListBestSellingProductCommand command = new ListBestSellingProductCommand(pageable);
        Page<Product> result = listBestSellingProductService.listBestSellingProductToady(command);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isZero();
    }

    @Test
    @DisplayName("페이지네이션이 정상적으로 동작한다")
    void listBestSellingProductsWithPagination() {
        // given
        LocalDateTime soldAt = LocalDateTime.now();
        
        // 상품1: 10개, 상품2: 20개, 상품3: 30개 판매
        rankProductService.rankProduct(new RankProductCommand(
                new OrderItemQuantities(Map.of(productId1, new OrderItemQuantity(10))),
                soldAt
        ));
        rankProductService.rankProduct(new RankProductCommand(
                new OrderItemQuantities(Map.of(productId2, new OrderItemQuantity(20))),
                soldAt
        ));
        rankProductService.rankProduct(new RankProductCommand(
                new OrderItemQuantities(Map.of(productId3, new OrderItemQuantity(30))),
                soldAt
        ));

        // when - 페이지 크기 2로 조회
        Pageable pageable = Pageable.ofSize(2);
        ListBestSellingProductCommand command = new ListBestSellingProductCommand(pageable);
        Page<Product> result = listBestSellingProductService.listBestSellingProductToady(command);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getTotalElements()).isEqualTo(3);
        assertThat(result.getTotalPages()).isEqualTo(2);
        
        // 첫 페이지: 상품3(30개), 상품2(20개)
        List<Product> products = result.getContent();
        assertThat(products.get(0).getProductId()).isEqualTo(productId3);
        assertThat(products.get(1).getProductId()).isEqualTo(productId2);
    }
} 