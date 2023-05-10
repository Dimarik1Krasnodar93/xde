package com.xde.service;

import com.xde.enums.StepType;
import com.xde.model.Event;
import com.xde.repository.DocInputRepository;
import com.xde.repository.EventRepository;
import com.xde.threads.runnableThreads.ThreadStepsQueryXDE;
import com.xde.threads.runnableThreads.ThreadRemoveSteps;
import com.xde.threads.runnableThreads.ThreadSaverResults;
import com.xde.xde.ConnectorToXDE;
import com.xde.xde.XDEContainer;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Supplier;

@Service
@AllArgsConstructor
public class StepService {
    private ConnectorToXDE connectorToXDE;
    private OrganizationBoxCountService organizationBoxCountService;
    private EventRepository eventRepository;
    private DocInputRepository docInputRepository;

    public void executeSteps(Supplier<List<Event>> supplier, StepType type) {
        XDEContainer xdeContainer = connectorToXDE.getXdeContainer();
        Thread[] threadRemoveSteps = new ThreadRemoveSteps[connectorToXDE.getProcessorsCount()];
        for (int i = 0; i < connectorToXDE.getProcessorsCount(); i++) {
            threadRemoveSteps[i] = new ThreadRemoveSteps(xdeContainer, type, i);
            threadRemoveSteps[i].start();
        }
        for (int i = 0; i < connectorToXDE.getProcessorsCount(); i++) {
            try {
                threadRemoveSteps[i].join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        List<Event> list = supplier.get();
        Thread[] threadContainers = new ThreadStepsQueryXDE[connectorToXDE.getProcessorsCount()];

        xdeContainer.addSteps(type, connectorToXDE.getProcessorsCount(), list, type);
        for (int i = 0; i < connectorToXDE.getProcessorsCount(); i++) {
            threadContainers[i] = new ThreadStepsQueryXDE(type, xdeContainer,
                    i);
            threadContainers[i].start();
        }
        for (int i = 0; i < connectorToXDE.getProcessorsCount(); i++) {
            try {
                threadContainers[i].join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        Thread[] threadSaver = new ThreadSaverResults[connectorToXDE.getProcessorsCount()];
        for (int i = 0; i < connectorToXDE.getProcessorsCount(); i++) {
            threadSaver[i] = new ThreadSaverResults(xdeContainer,
                    i, docInputRepository);
            threadSaver[i].start();
        }
        for (int i = 0; i < connectorToXDE.getProcessorsCount(); i++) {
            try {
                threadSaver[i].join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
