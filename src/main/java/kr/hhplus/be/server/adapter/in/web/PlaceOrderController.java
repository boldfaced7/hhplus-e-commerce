package kr.hhplus.be.server.adapter.in.web;

import kr.hhplus.be.server.application.port.in.PlaceOrderCommand;
import kr.hhplus.be.server.application.port.in.PlaceOrderUseCase;
import kr.hhplus.be.server.domain.model.Order;
import kr.hhplus.be.server.domain.vo.coupon.CouponId;
import kr.hhplus.be.server.domain.vo.orderitem.OrderItemQuantities;
import kr.hhplus.be.server.domain.vo.user.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class PlaceOrderController {

    private final PlaceOrderUseCase placeOrderUseCase;

    @PostMapping
    public ResponseEntity<Response> placeOrder(@RequestBody Request request) {
        PlaceOrderCommand command = toCommand(request);
        Order placedOrder = placeOrderUseCase.placeOrder(command);
        Response response = toResponse(placedOrder);

        return ResponseEntity.ok(response);
    }

    private PlaceOrderCommand toCommand(Request request) {
        return new PlaceOrderCommand(
                UUID.fromString(request.uuid()),
                new UserId(request.userId()),
                new CouponId(request.couponId()),
                OrderItemQuantities.create(request.orderItems)
        );
    }

    private Response toResponse(Order order) {
        return new Response(
                order.getOrderId().value(),
                "PENDING",
                order.getDiscountTotalPrice().value()
        );
    }

    public record Request(
            String uuid,
            Long userId,
            Long couponId,
            Map<Long, Integer> orderItems
    ) {}

    public record Response(
            Long orderId,
            String status,
            Long totalPrice
    ) {}
}