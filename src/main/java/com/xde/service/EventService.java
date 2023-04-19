package com.xde.service;

import com.xde.Status;
import com.xde.handlers.EventHandler;
import com.xde.model.Event;
import com.xde.model.OrganizationBox;
import com.xde.model.OrganizationBoxCount;
import com.xde.model.steps.Step;
import com.xde.repository.EventRepository;
import com.xde.threads.ThreadContainer;
import com.xde.threads.ThreadRemoveSteps;
import com.xde.xde.ConnectorToXDE;
import com.xde.xde.XDEContainer;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class EventService {
    private ConnectorToXDE connectorToXDE;
    private OrganizationBoxCountService organizationBoxCountService;
    private EventRepository eventRepository;

    private static Logger logger = LoggerFactory.getLogger(EventService.class);


    public String getEvents(int id) {
        OrganizationBoxCount organizationBoxCount = organizationBoxCountService.findById(id);
        String boxId = organizationBoxCount.getBox().getName();
        int lastMessage = organizationBoxCount.getCount();
        List<Map> list = connectorToXDE.getInputEvents(boxId, lastMessage);
        int maxEvent = saveAllFromXDE(list, organizationBoxCount.getBox());
        organizationBoxCount.setCount(maxEvent);
        organizationBoxCount.setCount(42352);
        organizationBoxCountService.save(organizationBoxCount);
        return "ok";
    }
    public void approveAll() {
        XDEContainer xdeContainer = connectorToXDE.getXdeContainer();
        ThreadRemoveSteps[] threadRemoveSteps = new ThreadRemoveSteps[connectorToXDE.getProcessorsCount()];
        for (int i = 0; i < connectorToXDE.getProcessorsCount(); i++) {
            threadRemoveSteps[i] = new ThreadRemoveSteps(xdeContainer, i);
            threadRemoveSteps[i].run();
        }
        List<Event> listApprove = eventRepository.getAllToExecute();
        ThreadContainer[] threadContainers = new ThreadContainer[connectorToXDE.getProcessorsCount()];
        xdeContainer.addStepsApprove(connectorToXDE.getProcessorsCount(), listApprove);
        for (int i = 0; i < connectorToXDE.getProcessorsCount(); i++) {
            threadContainers[i] = new ThreadContainer(xdeContainer,
                    i);
            threadContainers[i].run();
        }

    }
    private int saveAllFromXDE(List<Map> list, OrganizationBox organizationBox) {
        int result = 0;
        int i = 0;
        List<Event> eventList = new ArrayList<>(ConnectorToXDE.MAX_COUNT_EVENTS);
        for (Map map : list) {
            logger.info("______Start to parse " + i++);
            Event event = new Event();
            Map<String, Object> mapEvent = (Map) map.get("Event");
            Map<String, Object> mapStatusCode = (Map) mapEvent.get("StatusCode");
            Map<String, Object> mapContent = (Map) map.get("Content");
            event.setOrganizationBox(organizationBox);
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
            if (event.getEventId() > result) {
                result = event.getEventId();
            }
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
            Event eventFromRepository = eventRepository.findEventByOrganizationBoxAndDocId(organizationBox, event.getDocId());
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
        return result;
    }
}
