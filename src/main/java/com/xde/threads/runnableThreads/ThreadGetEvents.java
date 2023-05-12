package com.xde.threads.runnableThreads;

import com.xde.service.EventService;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@AllArgsConstructor
public class ThreadGetEvents extends Thread {
    private EventService eventService;
    private int id;
    private static Logger logger = LoggerFactory.getLogger(ThreadGetEvents.class);
    @SneakyThrows
    @Override
    public void run() {
        while (!interrupted()) {
            logger.info("start ThreadGetEvents");
            eventService.getEvents(id);
            Thread.sleep(10000);
            if (eventService.isFatalError()) {
                logger.info("interrupt ThreadGetEvents");
                Thread.currentThread().interrupt();
            }
        }
    }
}
