package com.xde.service;

import com.xde.Status;
import com.xde.enums.StepType;
import com.xde.handlers.EventHandler;
import com.xde.model.DocInput;
import com.xde.model.Event;
import com.xde.model.OrganizationBox;
import com.xde.model.OrganizationBoxCount;
import com.xde.repository.DocInputRepository;
import com.xde.repository.EventRepository;
import com.xde.threads.runnableThreads.*;
import com.xde.xde.ConnectorToXDE;
import com.xde.xde.XDEContainer;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Сервис событий
 * */
@Service
@AllArgsConstructor
public class EventService {
    private ConnectorToXDE connectorToXDE;
    private OrganizationBoxCountService organizationBoxCountService;
    private EventRepository eventRepository;
    private DocInputRepository docInputRepository;
    private StepService stepService;

    private static Logger logger = LoggerFactory.getLogger(EventService.class);

    public boolean isFatalError() {
        return connectorToXDE.isFatalError();
    }
    public String getEvents(int id) {
       // long timeStart = System.currentTimeMillis();
        OrganizationBoxCount organizationBoxCount = organizationBoxCountService.findById(id);
       // final int valueConst = 44560; //временно для отладки - удалить в дальнейшем
      //  organizationBoxCountService.save(organizationBoxCount); //временно для отладки  - удалить в дальнейшем
        String boxId = organizationBoxCount.getBox().getName();
        int lastMessage = organizationBoxCount.getCount();
        organizationBoxCount.setCount(lastMessage);
        organizationBoxCountService.save(organizationBoxCount);
       // lastMessage = valueConst;
        Set<Map> set = connectorToXDE.getInputEvents(boxId, lastMessage);
        int maxEvent = saveAllFromXDE(set, organizationBoxCount.getBox());
        if (maxEvent > lastMessage && maxEvent > 0 ) {
            organizationBoxCount.setCount(maxEvent);
            organizationBoxCountService.save(organizationBoxCount);
        }
      //  organizationBoxCount.setCount(valueConst);//временно для отладки - удалить в дальнейшем
        return "ok";
    }

    public void loadArchive() {
        List<Event> eventList = eventRepository.findAllInIAndNullData();
        ThreadLoaderArchive[] threadLoaderArchives = new ThreadLoaderArchive[connectorToXDE.getProcessorsCount()];
        for (int i = 0; i < connectorToXDE.getProcessorsCount(); i++) {
            threadLoaderArchives[i] = new ThreadLoaderArchive(connectorToXDE.getXdeContainer(), i, eventList);
            threadLoaderArchives[i].start();
        }
        for (int i = 0; i < connectorToXDE.getProcessorsCount(); i++) {
            try {
                threadLoaderArchives[i].join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void approveAll() {
        stepService.executeSteps(() -> eventRepository.getAllToExecute(), StepType.ACCEPT_INPUT);
    }

    public void createAll() {
        List<Event> list = eventRepository.getAllToCreate();
        List<String> listString = new ArrayList<>(list.size());
        for (Event event : list) {
            listString.add(event.getDocId());
        }
        List<DocInput> listInDb = docInputRepository.findByIdDocIn(listString);
        Iterator<Event> iterator = list.iterator();
        List<Event> listToCreate = new ArrayList<>(list.size());
       // Event event = null;
        boolean skipElement = false;
        for (Event event : list) {
            skipElement = false;
            for (DocInput docInput : listInDb) {
                if (event.getDocId().equals(docInput.getIdDoc())) {
                    skipElement = true;
                }
            }
            if (!skipElement) {
                listToCreate.add(event);
            }
        }
        stepService.executeSteps(() -> list, StepType.CREATE_INPUT);
    }

    public void updateAll() {
        List<Event> list = eventRepository.getAllToUpdate();
        Thread[] threadUpdaterStatuses = new ThreadUpdaterStatuses[connectorToXDE.getProcessorsCount()];
        for (int i = 0; i < connectorToXDE.getProcessorsCount(); i++) {
            threadUpdaterStatuses[i] = new ThreadUpdaterStatuses(eventRepository,
                    docInputRepository, i, connectorToXDE.getProcessorsCount(), list);
            threadUpdaterStatuses[i].start();
        }
        for (int i = 0; i < connectorToXDE.getProcessorsCount(); i++) {
            try {
                threadUpdaterStatuses[i].join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
    public boolean needWork() {
        return eventRepository.getAllToExecute().size() > 0
                || connectorToXDE.getXdeContainer().stepsAreWorking();
    }

    public boolean needCreate() {
        return eventRepository.getAllToExecute().size() > 0
                || connectorToXDE.getXdeContainer().stepsAreWorking(StepType.CREATE_INPUT);
    }
    private int saveAllFromXDE(Set<Map> set, OrganizationBox organizationBox) {
        int result = 0;
        int i = 0;
        List<Event> eventList = new ArrayList<>(ConnectorToXDE.MAX_COUNT_EVENTS);
        for (Map map : set) {
           // logger.info("______Start to parse " + i++);
            Event event = new Event();
            Map<String, Object> mapEvent = (Map) map.get("Event");
            Map<String, Object> mapStatusCode = (Map) mapEvent.get("StatusCode");
            Map<String, Object> mapContent = (Map) map.get("Content");
            event.setOrganizationBox(organizationBox);
            event.setDocId(map.get("DocumentId").toString());
            event.setCodeEvent(mapEvent.get("EventId").toString());
            //logger.info("Start to parse " + i + " docId " + event.getDocId());
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
               // logger.info("map content is null " + i + " docId " + event.getDocId());
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
