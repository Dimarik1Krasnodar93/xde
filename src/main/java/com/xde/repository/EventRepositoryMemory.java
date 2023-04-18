package com.xde.repository;

import com.xde.dto.BoxDocument;
import com.xde.model.Event;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.HashMap;

@Repository
public class EventRepositoryMemory implements EventRepository {
    private int id = 0;
    private HashMap<BoxDocument, Event> eventsStore = new HashMap<>();

    @Override
    public Event findEventByBoxIdAndDocId(String boxId, String docId) {
        BoxDocument boxDocument = new BoxDocument(boxId, docId);
        return eventsStore.get(boxDocument);
    }

    @Override
    public void save(Event event) {
        BoxDocument boxDocument = new BoxDocument(event.getBoxId(), event.getDocId());
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
        BoxDocument boxDocument = new BoxDocument(event.getBoxId(), event.getDocId());
        eventsStore.put(boxDocument, event);
    }
}
