package com.xde.threads.runnableThreads;

import com.xde.enums.StepType;
import com.xde.xde.XDEContainer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**Контейнер потоков для выполнения шагов задач
 * */
@Getter
@Setter
@AllArgsConstructor
public class ThreadStepsQueryXDE extends Thread {
    private StepType stepType;
    private volatile XDEContainer xdeContainer;
    private int processorValue;


    @Override
    public void run() {
        xdeContainer.execute(stepType, processorValue);
    }
}
