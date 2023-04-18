package com.xde.service;

import com.xde.Status;
import com.xde.handlers.EventHandler;
import com.xde.model.Event;
import com.xde.repository.EventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class EventService {
    private EventRepository eventRepository;

    Logger logger = LoggerFactory.getLogger(EventService.class);

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public void saveAllFromXDE(List<Map> list, String boxId) {
        int i = 0;
        List<Event> eventList = new ArrayList<>();
        for (Map map : list) {
            logger.info("______Start to parse " + i++);
            Event event = new Event();
            Map<String, Object> mapEvent = (Map) map.get("Event");
            Map<String, Object> mapStatusCode = (Map) mapEvent.get("StatusCode");
            Map<String, Object> mapContent = (Map) map.get("Content");
            event.setBoxId(boxId);
            event.setDocId(map.get("DocumentId").toString());
            event.setCodeEvent(mapEvent.get("EventId").toString());
            logger.info("Start to parse " + i + " docId " + event.getDocId());
            event.setOutputDoc((Boolean) mapEvent.get("IsOutput"));
            Status status = new Status(mapStatusCode.get("Value").toString());
            event.setStatus(status.getStatus());
            event.setPrintForm(status.getPrintForm());
            event.setDateTime(LocalDateTime.parse(((Map) map.get("Event"))
                    .get("OperatorDateTime")
                    .toString()
            ));
            event.setEventId((Integer) map.get("StatusId"));
            if (mapContent != null) {
                event.setLinkArchive(mapContent.get("LinkId").toString());
                event.setFileName(mapContent.get("Name").toString());
                event.setTypeEvent((Integer) mapContent.get("Type"));
                List<Map> signaturesList = (List) mapContent.get("Signatures");
                if (signaturesList != null && signaturesList.size() > 0) {
                    Map<String, Object> mapCertificate = (Map) signaturesList.get(0).get("Certificate");
                    event.setCertificateSFM(mapCertificate.get("Surname").toString() + " "
                            + mapCertificate.get("FirstName").toString() + " "
                            + mapCertificate.get("MiddleName").toString() + " ");
                    event.setCertificateDateFrom(LocalDateTime.parse(mapCertificate.get("ValidFrom").toString()));
                    event.setCertificateDateTo(LocalDateTime.parse(mapCertificate.get("ValidTo").toString()));
                    event.setCertificateNumber(mapCertificate.get("SerialNumber").toString());
                    event.setCertificateThumbprint(mapCertificate.get("Thumbprint").toString());
                }
            } else {
                logger.info("map content is null " + i + " docId " + event.getDocId());
            }
            Event eventFromRepository = eventRepository.findEventByBoxIdAndDocId(boxId, event.getDocId());
            if (eventFromRepository == null) {
                eventList.add(event);
            } else {
                if (EventHandler.getPriority(event.getStatus())
                        >= EventHandler.getPriority(eventFromRepository.getStatus())) {
                    eventRepository.save(eventFromRepository);
                }
            }
        }
        eventRepository.saveAll(eventList);
    }
}
