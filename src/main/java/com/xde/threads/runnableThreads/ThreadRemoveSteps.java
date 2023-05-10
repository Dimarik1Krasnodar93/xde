package com.xde.threads.runnableThreads;

import com.xde.enums.StepType;
import com.xde.xde.XDEContainer;
import lombok.AllArgsConstructor;

/**
 * Контейнер потоков для удаления выполненных задач
 * */
@AllArgsConstructor
public class ThreadRemoveSteps extends Thread {
    private volatile XDEContainer xdeContainer;
    private StepType stepType;
    private int processorValue;

    @Override
    public void run() {
        xdeContainer.removeDoneSteps(stepType, processorValue);
    }
}
