package com.xde.threads.runnableThreads;

import com.xde.service.EventService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

@AllArgsConstructor
public class ThreadApproveAll extends Thread {
    private EventService eventService;
    private static Logger logger = LoggerFactory.getLogger(ThreadApproveAll.class);
    @Override
    public void run() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        logger.info("+++START APPROVE " + LocalDateTime.now());
        while (!interrupted()) {
            eventService.approveAll();
//            if (!eventService.needWork()) {
//                logger.info("+++FINISH APPROVE " + LocalDateTime.now());
//                Thread.currentThread().interrupt();
//
//            }
        }
    }
}
