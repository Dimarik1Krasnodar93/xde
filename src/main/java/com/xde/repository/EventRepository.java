package com.xde.repository;

import com.xde.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;

public interface EventRepository {
    Event findEventByBoxIdAndDocId(String boxId, String docId);
    void save(Event event);
    void saveAll(Collection<Event> events);
    void update(Event event);
}
