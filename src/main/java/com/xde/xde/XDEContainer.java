package com.xde.xde;

import com.xde.enums.StepType;
import com.xde.model.Event;
import com.xde.model.steps.Step;
import com.xde.model.steps.StepCreateDocInput;
import com.xde.model.steps.StepsApprove1CSign;
import com.xde.service.EventService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.xde.enums.StepType.*;

/**
*Класс содержит очереди для работы с событиями
*хранятся события в Map<Map>. Integer - сделано для разделения потоков
* */
@Getter
public class XDEContainer {
    private Map<StepType, Map<Integer, List<Step>>> map = new ConcurrentHashMap<>();
    private  ConnectorToXDE connectorToXDE;
    private static Logger logger = LoggerFactory.getLogger(EventService.class);
    public XDEContainer(int processorsCount, ConnectorToXDE connectorToXDE) {
        this.connectorToXDE = connectorToXDE;
        for (StepType stepType : StepType.values()) {
            Map<Integer, List<Step>> mapTemp = new ConcurrentHashMap<>();
            for (int i = 0; i < processorsCount; i++) {
                mapTemp.put(i, new ArrayList<>(ConnectorToXDE.MAX_COUNT_EVENTS));
            }
            map.put(stepType, mapTemp);
        }
    }

    public void execute(StepType stepType, int mapNumber) {
      Map<Integer, List<Step>> mapStepType = map.get(stepType);
      List<Step> steps = mapStepType.get(mapNumber);
      steps.parallelStream().forEach(i -> connectorToXDE.executeStep(i));
    }

    private void saveStepResults(Step step) {
       // String result = getStepResult
    }


    public void addStepsApprove(int totalProcessors, List<Event> listEvent) {
        Map<Integer, List<Step>> mapStepType = map.get(ACCEPT_INPUT);
        for (Event event : listEvent) {
            event.setStartedExecution(true);
            List<Step> list = mapStepType.get(event.getEventId() % totalProcessors);
            list.add(new StepsApprove1CSign(true, event));
        }
    }

    public void addSteps(StepType stepType, int totalProcessors, List<Event> listEvent, StepType type) {
        Map<Integer, List<Step>> mapStepType = map.get(stepType);
        for (Event event : listEvent) {
            List<Step> list = mapStepType.get(event.getEventId() % totalProcessors);
            switch (type) {
                case ACCEPT_INPUT:
                    event.setStartedExecution(true);
                    list.add(new StepsApprove1CSign(true, event));
                    break;
                case CREATE_INPUT:
                    event.setStartedCreation(true);
                    list.add(new StepCreateDocInput(event));
                    break;
                default: list.add(new StepsApprove1CSign(true, event));
            }
        }
    }

    public void removeDoneSteps(StepType stepType, int processor) {
        Map<Integer, List<Step>> mapStepType = map.get(stepType);
        for (int i = 0; i < processor; i++) {
            List<Step> list = mapStepType.get(i);
            Iterator<Step> iterator = list.iterator();
            while (iterator.hasNext()) {
                Step step = iterator.next();
                if (step.getDone() && step.getSavedResults()) {
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

    public boolean stepsAreWorking(StepType stepType) {
        boolean result = false;
        Map<Integer, List<Step>> mapStepType = map.get(stepType);
        for (Map.Entry<Integer, List<Step>> entry : mapStepType.entrySet()) {
            result = result || entry.getValue().size() > 0;
        }
        return result;
    }

    public boolean stepsAreWorking() {
        boolean result = false;
        for (StepType stepType : StepType.values()) {
            Map<Integer, List<Step>> mapStepType = map.get(stepType);
            for (Map.Entry<Integer, List<Step>> entry : mapStepType.entrySet()) {
                result = result || entry.getValue().size() > 0;
            }
        }
        return result;
    }
}
