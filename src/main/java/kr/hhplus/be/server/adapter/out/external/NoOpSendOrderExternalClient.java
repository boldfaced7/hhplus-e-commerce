package kr.hhplus.be.server.adapter.out.external;

import kr.hhplus.be.server.domain.event.order.OrderPlaced;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NoOpSendOrderExternalClient implements SendOrderExternalClient {

    @Override
    public void sendOrder(OrderPlaced event) {
        log.info("주문 이벤트 전송: {}", event);
    }

}
