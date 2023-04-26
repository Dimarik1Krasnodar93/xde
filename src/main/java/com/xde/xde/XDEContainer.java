package com.xde.xde;

import com.xde.model.Event;
import com.xde.model.steps.Step;
import com.xde.model.steps.StepsApprove;
import com.xde.service.EventService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
*Класс содержит очереди для работы с событиями
*хранятся события в Map<Map>. Integer - сделано для разделения потоков
* */
public class XDEContainer {
    private volatile   Map<Integer, List<Step>> map = new ConcurrentHashMap<>();
    private  ConnectorToXDE connectorToXDE;
    private static Logger logger = LoggerFactory.getLogger(EventService.class);
    public XDEContainer(int processorsCount, ConnectorToXDE connectorToXDE) {
        this.connectorToXDE = connectorToXDE;
        for (int i = 0; i < processorsCount; i++) {
            this.map.put(i, new ArrayList<>(ConnectorToXDE.MAX_COUNT_EVENTS));
        }
    }

    public void execute(int mapNumber) {
      List<Step> steps = map.get(mapNumber);
      steps.parallelStream().forEach(i -> connectorToXDE.executeStep(i));
      //отладка удалить - для выполнения в 1 потоке
      //execute in 1 Thread
//      for (Step step : steps) {
//          long start = System.currentTimeMillis();
//          connectorToXDE.executeStep(step);
//          long end = System.currentTimeMillis();
//          long res = end - start;
//          logger.info("=============" + res);
//      }
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

    public boolean stepsAreWorking() {
        boolean result = false;
        for (Map.Entry<Integer, List<Step>> entry : map.entrySet()) {
            result = result || entry.getValue().size() > 0;
        }
        return result;
    }
}
