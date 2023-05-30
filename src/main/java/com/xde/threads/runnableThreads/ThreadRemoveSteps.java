package com.xde.threads.runnableThreads;

import com.xde.enums.StepType;
import com.xde.xde.XDEContainer;
import lombok.AllArgsConstructor;

/**
 * Контейнер потоков для удаления выполненных задач
 * */
public class ThreadRemoveSteps extends Thread {
    private volatile XDEContainer xdeContainer;
    private StepType stepType;
    private int processorValue;

    public ThreadRemoveSteps(XDEContainer xdeContainer, StepType stepType, int processorValue, String name) {
        this.xdeContainer = xdeContainer;
        this.stepType = stepType;
        this.processorValue = processorValue;
        this.setName(name);
    }

    @Override
    public void run() {
        xdeContainer.removeDoneSteps(stepType, processorValue);
    }
}
