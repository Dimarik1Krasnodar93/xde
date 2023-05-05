package com.xde.threads.runnableThreads;

import com.xde.service.EventService;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;

@AllArgsConstructor
public class ThreadUpdateAll extends Thread {
    private EventService eventService;
    @SneakyThrows
    @Override
    public void run() {
        while (!interrupted()) {
            eventService.updateAll();
            Thread.sleep(150);
            if (eventService.isFatalError()) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
