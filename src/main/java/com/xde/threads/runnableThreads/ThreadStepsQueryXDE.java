package com.xde.threads.runnableThreads;

import com.xde.enums.StepType;
import com.xde.xde.XDEContainer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**Контейнер потоков для выполнения шагов задач
 * */

public class ThreadStepsQueryXDE extends Thread {
    private StepType stepType;
    private volatile XDEContainer xdeContainer;
    private int processorValue;

    public ThreadStepsQueryXDE(StepType stepType, XDEContainer xdeContainer, int processorValue, String name) {
        this.stepType = stepType;
        this.xdeContainer = xdeContainer;
        this.processorValue = processorValue;
        this.setName(name);
    }

    @Override
    public void run() {
        xdeContainer.execute(stepType, processorValue);
    }
}
