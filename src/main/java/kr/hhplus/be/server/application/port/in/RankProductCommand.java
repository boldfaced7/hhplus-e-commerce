package kr.hhplus.be.server.application.port.in;

import kr.hhplus.be.server.domain.vo.orderitem.OrderItemQuantities;

import java.time.LocalDateTime;

public record RankProductCommand(
        OrderItemQuantities quantities,
        LocalDateTime soldAt
) {
}
