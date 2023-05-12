package com.xde.threads.runnableThreads;

import com.xde.service.EventService;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

@AllArgsConstructor
public class ThreadCreateDocInput extends Thread {
    private EventService eventService;
    private static Logger logger = LoggerFactory.getLogger(ThreadCreateDocInput.class);
    @SneakyThrows
    @Override
    public void run() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        while (!interrupted()) {
            try {
                Thread.sleep(4000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            logger.info("start ThreadCreateDocInput");
            eventService.createAll();
            if (!eventService.needCreate()) {
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            if (eventService.isFatalError()) {
                logger.error("interrupt thread create doc input");
                Thread.currentThread().interrupt();
            }
        }
        logger.error("interrupt thread create doc input end");
    }
}
