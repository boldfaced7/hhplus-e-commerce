package kr.hhplus.be.server.adapter.out.event;

import kr.hhplus.be.server.application.port.out.event.PublishEventPort;
import kr.hhplus.be.server.domain.model.EventDomain;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ApplicationEventPublishingAdapter implements PublishEventPort {

    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void publishEvents(EventDomain eventDomain) {
        var pulled = eventDomain.pullEvents();
        log.info("pulled: {}", pulled);
        pulled.forEach(applicationEventPublisher::publishEvent);
    }
}
