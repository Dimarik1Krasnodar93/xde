package com.xde.threads.runnableThreads;

import com.xde.service.EventService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

public class ThreadApproveAll extends Thread {
    private EventService eventService;
    private static Logger logger = LoggerFactory.getLogger(ThreadApproveAll.class);

    public ThreadApproveAll(EventService eventService, String name) {
        this.eventService = eventService;
        this.setName(name);
    }

    @Override
    public void run() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        logger.info("+++START APPROVE " + LocalDateTime.now());
        int count = 0;
        while (!interrupted()) {
//            count ++;
//            if (count % 2 == 0) {
//                int countToExecute = eventService.CountEventsToExecute();
//                if (countToExecute != 0) {
//                    logger.info("count " + countToExecute);
//                }
//            }
            eventService.approveAll();
            if (!eventService.needWork()) {
                try {
                    Thread.sleep(10);
                   // logger.info("+++SLEEP " + LocalDateTime.now());
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            if (eventService.isFatalError()) {
                logger.error("interrupt thread approve all");
               // Thread.currentThread().interrupt();
            }
//            if (!eventService.needWork()) {
//                logger.info("+++FINISH APPROVE " + LocalDateTime.now());
//                Thread.currentThread().interrupt();
//
//            }
        }
    }
}
