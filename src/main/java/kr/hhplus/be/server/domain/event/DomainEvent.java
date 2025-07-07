package kr.hhplus.be.server.domain.event;

import kr.hhplus.be.server.domain.vo.order.Guid;

public interface DomainEvent {
    Guid guid();
}
