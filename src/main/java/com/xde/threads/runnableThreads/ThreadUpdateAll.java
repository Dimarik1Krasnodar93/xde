package com.xde.threads.runnableThreads;

import com.xde.service.EventService;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ThreadUpdateAll extends Thread {
    private EventService eventService;
    private static Logger logger = LoggerFactory.getLogger(ThreadUpdateAll.class);

    public ThreadUpdateAll(EventService eventService, String name) {
        this.eventService = eventService;
        this.setName(name);
    }

    @SneakyThrows
    @Override
    public void run() {
        while (!interrupted()) {
            logger.info("start ThreadUpdateAll");
            eventService.updateAll();
            Thread.sleep(1500);
            if (eventService.isFatalError()) {
                logger.info("interrupt ThreadUpdateAll");
                Thread.currentThread().interrupt();
            }
        }
    }
}
