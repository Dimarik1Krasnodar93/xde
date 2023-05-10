package com.xde.repository;

import com.xde.dto.BoxDocument;
import com.xde.handlers.EventHandler;
import com.xde.model.DocInput;
import com.xde.model.Event;
import com.xde.model.OrganizationBox;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Репозиторий событий в памяти
 * */
@Repository
public class EventRepositoryMemory implements EventRepository {
    private DocInputRepository docInputRepository;
    private static int id = 0;

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
        Event eventFromRepository = eventsStore.get(boxDocument);
        if (eventFromRepository == null) {
            eventsStore.put(boxDocument, event);
        } else {
            if (EventHandler.getPriority(event.getStatus())
                    > EventHandler.getPriority(eventFromRepository.getStatus())) {
                eventsStore.put(boxDocument, event);
            }
        }
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
                .filter(i -> !i.isStartedExecution()  && "I".equals(i.getStatus()))
                .toList();
    }

    @Override
    public List<Event> getAllToCreate() {
         return  eventsStore.values().stream()
                .filter(i -> !i.isStartedCreation())
                .toList();
    }

    @Override
    public List<Event> getAllToUpdate() {
        return eventsStore.values().stream()
                .filter(i -> !i.isStartedExecution()  && !"I".equals(i.getStatus()))
                .toList();
    }

    @Override
    public List<Event> findAllInIAndNullData() {
        return eventsStore.values().stream()
                .filter(i -> i.getData() == null && "I".equals(i.getStatus()))
                .toList();
    }

    @Override
    public List<Event> findAll() {
        return eventsStore.values().stream()
                .toList();
    }
}
