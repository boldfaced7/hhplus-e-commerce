package kr.hhplus.be.server.application.port.out.event;

import kr.hhplus.be.server.domain.model.EventDomain;

public interface PublishEventPort {
    void publishEvents(EventDomain eventDomain);
}
