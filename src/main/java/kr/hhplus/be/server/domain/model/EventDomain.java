package kr.hhplus.be.server.domain.model;

import kr.hhplus.be.server.domain.event.DomainEvent;

import java.util.ArrayList;
import java.util.List;

public class EventDomain {

    private final List<DomainEvent> events = new ArrayList<>();

    public List<DomainEvent> pullEvents() {
        var pulled = List.copyOf(events);
        events.clear();
        return pulled;
    }

    public void addEvent(DomainEvent event) {
        events.add(event);
    }
}
