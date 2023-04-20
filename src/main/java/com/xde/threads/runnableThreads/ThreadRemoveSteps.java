package com.xde.threads.runnableThreads;

import com.xde.xde.XDEContainer;
import lombok.AllArgsConstructor;

/**
 * Контейнер потоков для удаления выполненных задач
 * */
@AllArgsConstructor
public class ThreadRemoveSteps extends Thread {
    private volatile XDEContainer xdeContainer;
    private int processorValue;

    @Override
    public void run() {
        xdeContainer.removeDoneSteps(processorValue);
    }
}
