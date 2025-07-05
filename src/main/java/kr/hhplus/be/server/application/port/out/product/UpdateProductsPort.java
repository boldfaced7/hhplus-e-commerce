package kr.hhplus.be.server.application.port.out.product;

import kr.hhplus.be.server.domain.model.Products;

public interface UpdateProductsPort {
    Products updateProducts(Products products);
}
