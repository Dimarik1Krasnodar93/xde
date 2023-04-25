package com.xde.threads.runnableThreads;

import com.xde.service.EventService;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ThreadGetEvents extends Thread {
    private EventService eventService;
    private int id;
    @Override
    public void run() {
        while (!interrupted()) {
            eventService.getEvents(id);
        }
    }
}
