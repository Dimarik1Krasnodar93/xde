package com.xde.repository;

import com.xde.model.Event;
import com.xde.model.OrganizationBox;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface EventRepository {
    Event findEventByOrganizationBoxAndDocId(OrganizationBox organizationBox, String docId);
    void save(Event event);
    void saveAll(Collection<Event> events);
    void update(Event event);
    List<Event> getAllToExecute();
    List<Event> findAllInIAndNullData();
    List<Event> findAll();
}
