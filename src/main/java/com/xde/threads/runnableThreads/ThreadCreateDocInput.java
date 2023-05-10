package com.xde.threads.runnableThreads;

import com.xde.service.EventService;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;

import java.time.LocalDateTime;

@AllArgsConstructor
public class ThreadCreateDocInput extends Thread {
    private EventService eventService;
    @SneakyThrows
    @Override
    public void run() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        while (!interrupted()) {
            eventService.createAll();
            if (!eventService.needCreate()) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            if (eventService.isFatalError()) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
