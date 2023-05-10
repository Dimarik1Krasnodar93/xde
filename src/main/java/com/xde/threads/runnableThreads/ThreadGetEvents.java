package com.xde.threads.runnableThreads;

import com.xde.service.EventService;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;

@AllArgsConstructor
public class ThreadGetEvents extends Thread {
    private EventService eventService;
    private int id;
    @SneakyThrows
    @Override
    public void run() {
        while (!interrupted()) {
            eventService.getEvents(id);
            Thread.sleep(10000);
            if (eventService.isFatalError()) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
