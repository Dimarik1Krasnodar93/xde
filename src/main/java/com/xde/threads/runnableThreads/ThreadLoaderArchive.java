package com.xde.threads.runnableThreads;

import com.xde.model.Event;
import com.xde.xde.XDEContainer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class ThreadLoaderArchive extends Thread {
    private volatile XDEContainer xdeContainer;
    private int processorValue;
    public List<Event> eventList;

    public ThreadLoaderArchive(XDEContainer xdeContainer, int processorValue, List<Event> eventList, String name) {
        this.xdeContainer = xdeContainer;
        this.processorValue = processorValue;
        this.eventList = eventList;
        this.setName(name);
    }

    @Override
    public void run() {
        xdeContainer.loadArchiveAllEvents(processorValue, eventList);
    }

    public XDEContainer getXdeContainer() {
        return xdeContainer;
    }

    public int getProcessorValue() {
        return processorValue;
    }

    public List<Event> getEventList() {
        return eventList;
    }

    public void setXdeContainer(XDEContainer xdeContainer) {
        this.xdeContainer = xdeContainer;
    }

    public void setProcessorValue(int processorValue) {
        this.processorValue = processorValue;
    }

    public void setEventList(List<Event> eventList) {
        this.eventList = eventList;
    }
}
