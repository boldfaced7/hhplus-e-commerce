package kr.hhplus.be.server.adapter.in.web;

import kr.hhplus.be.server.application.port.in.ListProductCommand;
import kr.hhplus.be.server.application.port.in.ListProductQuery;
import kr.hhplus.be.server.domain.model.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ListProductController {

    private final ListProductQuery listProductQuery;

    @GetMapping
    public ResponseEntity<Page<ProductDto>> listProducts(Pageable pageable) {
        ListProductCommand command = new ListProductCommand(pageable);
        Page<Product> productPage = listProductQuery.listProduct(command);
        Page<ProductDto> response = productPage.map(this::toDto);

        return ResponseEntity.ok(response);
    }

    private ProductDto toDto(Product product) {
        return new ProductDto(
                product.getProductId().value(),
                product.getName().value(),
                product.getPrice().value(),
                product.getStock().value()
        );
    }

    public record ProductDto(
            Long id,
            String name,
            Long price,
            Long stock
    ) {}
}