package kr.hhplus.be.server.adapter.in.web;

import kr.hhplus.be.server.application.port.in.PlaceOrderCommand;
import kr.hhplus.be.server.application.port.in.PlaceOrderUseCase;
import kr.hhplus.be.server.domain.model.Order;
import kr.hhplus.be.server.domain.vo.coupon.CouponId;
import kr.hhplus.be.server.domain.vo.order.Guid;
import kr.hhplus.be.server.domain.vo.orderitem.OrderItemQuantity;
import kr.hhplus.be.server.domain.vo.product.ProductId;
import kr.hhplus.be.server.domain.vo.user.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.stream.Collectors;

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
        Map<ProductId, OrderItemQuantity> orderItemQuantities = request.orderItems().entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> new ProductId(entry.getKey()),
                        entry -> new OrderItemQuantity(entry.getValue())
                ));

        return new PlaceOrderCommand(
                new Guid(request.guid()),
                new UserId(request.userId()),
                new CouponId(request.couponId()),
                orderItemQuantities
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
            Long guid,
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