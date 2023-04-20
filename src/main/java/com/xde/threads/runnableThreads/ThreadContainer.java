package com.xde.threads.runnableThreads;

import com.xde.xde.XDEContainer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**Контейнер потоков для выполнения шагов задач
 * */
@Getter
@Setter
@AllArgsConstructor
public class ThreadContainer extends Thread {
    private volatile XDEContainer xdeContainer;
    private int processorValue;

    @Override
    public void run() {

        xdeContainer.execute(processorValue);
    }
}
