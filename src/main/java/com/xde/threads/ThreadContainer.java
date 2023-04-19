package com.xde.threads;

import com.xde.xde.XDEContainer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ThreadContainer implements Runnable {
    private volatile XDEContainer xdeContainer;
    private int processorValue;

    @Override
    public void run() {
        xdeContainer.execute(processorValue);
    }
}
