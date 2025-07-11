package kr.hhplus.be.server.application.port.in;

import org.springframework.data.domain.Pageable;

public record ListBestSellingProductCommand(
        Pageable pageable
) {

}
