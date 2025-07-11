package kr.hhplus.be.server.adapter.out.external;

import kr.hhplus.be.server.domain.event.order.OrderPlaced;

public interface SendOrderExternalClient {
    void sendOrder(OrderPlaced event);
}
