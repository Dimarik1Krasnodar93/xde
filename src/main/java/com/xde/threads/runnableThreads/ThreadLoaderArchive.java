package com.xde.threads.runnableThreads;

import com.xde.model.Event;
import com.xde.xde.XDEContainer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ThreadLoaderArchive extends Thread {
    private volatile XDEContainer xdeContainer;
    private int processorValue;
    public List<Event> eventList;

    @Override
    public void run() {
        xdeContainer.loadArchiveAllEvents(processorValue, eventList);
    }
}
