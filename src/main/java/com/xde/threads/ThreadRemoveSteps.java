package com.xde.threads;

import com.xde.xde.XDEContainer;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ThreadRemoveSteps implements Runnable {
    private volatile XDEContainer xdeContainer;
    private int processorValue;

    @Override
    public void run() {
        xdeContainer.removeDoneSteps(processorValue);
    }
}
