package kr.hhplus.be.server.application.service.saga.balance;

import kr.hhplus.be.server.domain.vo.order.OrderDiscountTotalPrice;
import kr.hhplus.be.server.domain.vo.user.UserId;

import java.util.UUID;

public record DeductBalanceCommand(
        UUID uuid,
        UserId userId,
        OrderDiscountTotalPrice price
) {
}