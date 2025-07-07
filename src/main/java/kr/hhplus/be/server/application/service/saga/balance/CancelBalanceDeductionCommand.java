package kr.hhplus.be.server.application.service.saga.balance;

import kr.hhplus.be.server.domain.vo.order.Guid;
import kr.hhplus.be.server.domain.vo.order.OrderDiscountTotalPrice;
import kr.hhplus.be.server.domain.vo.user.UserId;

public record CancelBalanceDeductionCommand(
        Guid guid,
        UserId userId,
        OrderDiscountTotalPrice price
) {
}
