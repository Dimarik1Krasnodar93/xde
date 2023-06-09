package com.xde.service;

import com.xde.enums.StepType;
import com.xde.model.Event;
import com.xde.repository.EventRepository;
import com.xde.threads.runnableThreads.ThreadStepsQueryXDE;
import com.xde.threads.runnableThreads.ThreadRemoveSteps;
import com.xde.xde.ConnectorToXDE;
import com.xde.xde.XDEContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DocInputService {
    private ConnectorToXDE connectorToXDE;
    private OrganizationBoxCountService organizationBoxCountService;
    private EventRepository eventRepository;

    private static Logger logger = LoggerFactory.getLogger(EventService.class);

    public void createAll() {
        XDEContainer xdeContainer = connectorToXDE.getXdeContainer();
        ThreadRemoveSteps[] threadRemoveSteps = new ThreadRemoveSteps[connectorToXDE.getProcessorsCount()];
        for (int i = 0; i < connectorToXDE.getProcessorsCount(); i++) {
            threadRemoveSteps[i] = new ThreadRemoveSteps(xdeContainer, StepType.CREATE_INPUT, i, "t_RemoveSteps" + i);
            threadRemoveSteps[i].start();
        }
        for (int i = 0; i < connectorToXDE.getProcessorsCount(); i++) {
            try {
                threadRemoveSteps[i].join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        List<Event> listApprove = eventRepository.getAllToExecute();
        ThreadStepsQueryXDE[] threadContainers = new ThreadStepsQueryXDE[connectorToXDE.getProcessorsCount()];
        xdeContainer.addStepsApprove(connectorToXDE.getProcessorsCount(), listApprove);
        for (int i = 0; i < connectorToXDE.getProcessorsCount(); i++) {
            threadContainers[i] = new ThreadStepsQueryXDE(StepType.CREATE_INPUT, xdeContainer,
                    i, String.format("t_%s_%d", StepType.CREATE_INPUT, i) );
            threadContainers[i].start();
        }
        for (int i = 0; i < connectorToXDE.getProcessorsCount(); i++) {
            try {
                threadContainers[i].join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

    }
}
