package kr.hhplus.be.server.adapter.in.event;

import kr.hhplus.be.server.adapter.out.external.SendOrderExternalClient;
import kr.hhplus.be.server.domain.event.order.OrderPlaced;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class SendOrderEventHandler {

    private final SendOrderExternalClient sendOrderExternalClient;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void sendOrder(OrderPlaced event) {
        sendOrderExternalClient.sendOrder(event);
    }
}
