package com.xde.repository;

import com.xde.dto.BoxDocument;
import com.xde.model.Event;
import com.xde.model.OrganizationBox;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class EventRepositoryMemory implements EventRepository {
    private int id = 0;
    private Map<BoxDocument, Event> eventsStore = new ConcurrentHashMap<>();

    @Override
    public Event findEventByOrganizationBoxAndDocId(OrganizationBox organizationBox, String docId) {
        BoxDocument boxDocument = new BoxDocument(organizationBox, docId);
        return eventsStore.get(boxDocument);
    }

    @Override
    public void save(Event event) {
        BoxDocument boxDocument = new BoxDocument(event.getOrganizationBox(), event.getDocId());
        event.setId(++id);
        eventsStore.put(boxDocument, event);
    }

    @Override
    public void saveAll(Collection<Event> events) {
        for (Event event : events) {
            save(event);
        }
    }

    @Override
    public void update(Event event) {
        BoxDocument boxDocument = new BoxDocument(event.getOrganizationBox(), event.getDocId());
        eventsStore.put(boxDocument, event);
    }

    @Override
    public List<Event> getAllToExecute() {
        return eventsStore.values().stream()
                .filter(i -> !i.isStartedExecution() && "I".equals(i.getStatus()))
                .toList();
    }
}
