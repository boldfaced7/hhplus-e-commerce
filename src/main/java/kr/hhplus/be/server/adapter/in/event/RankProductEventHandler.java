package kr.hhplus.be.server.adapter.in.event;

import kr.hhplus.be.server.application.port.in.RankProductCommand;
import kr.hhplus.be.server.application.port.in.RankProductUseCase;
import kr.hhplus.be.server.domain.event.order.OrderPlaced;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class RankProductEventHandler {

    private final RankProductUseCase rankProductUseCase;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void rankProduct(OrderPlaced event) {
        var command = toCommand(event);
        rankProductUseCase.rankProduct(command);
    }

    private static RankProductCommand toCommand(OrderPlaced event) {
        return new RankProductCommand(
                event.quantities(),
                event.placedAt()
        );
    }
}
