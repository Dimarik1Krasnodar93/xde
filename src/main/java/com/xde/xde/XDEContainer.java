package com.xde.xde;

import com.xde.model.Event;
import com.xde.model.steps.Step;
import com.xde.model.steps.StepsApprove;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import javax.print.attribute.standard.Media;
import java.lang.reflect.Array;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
*Класс содержит очереди для работы с событиями
*хранятся события в Map<Map>. Integer - сделано для разделения потоков
* */

public class XDEContainer {
    private  Map<Integer, List<Step>> map = new ConcurrentHashMap<>();
    private  ConnectorToXDE connectorToXDE;

    public XDEContainer(int processorsCount, ConnectorToXDE connectorToXDE) {
        this.connectorToXDE = connectorToXDE;
        for (int i = 0; i < processorsCount; i++) {
            this.map.put(i, new ArrayList<>(ConnectorToXDE.MAX_COUNT_EVENTS));
        }
    }

    public void execute(int mapNumber) {
      List<Step> steps = map.get(mapNumber);
      for (Step step : steps) {
          connectorToXDE.executeStep(step);
      }
    }

    public void addStepsApprove(int totalProcessors, List<Event> listEvent) {
        for (Event event : listEvent) {
            event.setStartedExecution(true);
            List<Step> list = map.get(event.getEventId() % totalProcessors);
            list.add(new StepsApprove(true, event));
        }
    }

    public void removeDoneSteps(int processor) {
        for (int i = 0; i < processor; i++) {
            List<Step> list = map.get(i);
            Iterator<Step> iterator = list.iterator();
            while (iterator.hasNext()) {
                Step step = iterator.next();
                if (step.getDone()) {
                    iterator.remove();
                }
            }
        }
    }

    public void loadArchiveAllEvents(int processor, List<Event> eventList) {
        for (int i = processor; i < eventList.size(); i += connectorToXDE.getProcessorsCount()) {
            Event event = eventList.get(i);
            String archive = connectorToXDE.getEventArchive(event);
            event.setData(archive.getBytes());
        }
    }


}
