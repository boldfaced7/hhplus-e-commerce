package kr.hhplus.be.server.adapter.in.web;

import kr.hhplus.be.server.application.port.in.ListBestSellingProductCommand;
import kr.hhplus.be.server.application.port.in.ListBestSellingProductQuery;
import kr.hhplus.be.server.domain.model.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/products/top")
@RequiredArgsConstructor
public class ListBestSellingProductTodayController {

    private final ListBestSellingProductQuery listProductQuery;

    @GetMapping
    public ResponseEntity<Page<Response>> listProducts(Pageable pageable) {
        ListBestSellingProductCommand command = new ListBestSellingProductCommand(pageable);
        Page<Product> productPage = listProductQuery.listBestSellingProductToady(command);
        Page<Response> response = productPage.map(this::toDto);

        return ResponseEntity.ok(response);
    }

    private Response toDto(Product product) {
        return new Response(
                product.getProductId().value(),
                product.getName().value(),
                product.getPrice().value(),
                product.getStock().value()
        );
    }

    public record Response(
            Long id,
            String name,
            Long price,
            Long stock
    ) {}
}