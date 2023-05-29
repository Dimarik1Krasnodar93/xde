package com.xde.controller;

import com.xde.service.EventService;
import com.xde.threads.runnableThreads.ThreadApproveAll;
import com.xde.threads.runnableThreads.ThreadCreateDocInput;
import com.xde.threads.runnableThreads.ThreadGetEvents;
import com.xde.threads.runnableThreads.ThreadUpdateAll;
import com.xde.xde.ConnectorToXDE;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

/**
 * Контроллеры для получения входящих данных из xDE
 */
@RestController
@RequestMapping("/event")
@AllArgsConstructor
@Slf4j
public class EventController {
    private static Logger logger = LoggerFactory.getLogger(ConnectorToXDE.class);

    EventService eventService;
    StepController stepController;


    @PostMapping("/getEvents/{id}")
    public Object getData(@PathVariable("id") int id) {
        Thread threadGetEvents = new ThreadGetEvents(eventService, id);
        Thread threadCreateDocInput = new ThreadCreateDocInput(eventService);
        threadGetEvents.start();
        threadCreateDocInput.start();
        return "getData: ok";
    }

    @PostMapping("/archive")
    public Object loadArchive() {
        eventService.loadArchive();
        return "loadArchive: ok";
    }

    @PostMapping("/approve")
    public void approve() {

        eventService.approveAll();
    }

    @PostMapping("/getAllAndApprove/{id}")
    public void getAndApprove(@PathVariable("id") int id) throws InterruptedException {
        Thread threadGetEvents = new ThreadGetEvents(eventService, id);
        Thread threadApproveAll = new ThreadApproveAll(eventService);
        Thread threadCreateDocInput = new ThreadCreateDocInput(eventService);
        Thread threadUpdateAll = new ThreadUpdateAll(eventService);
        threadGetEvents.start();
        threadApproveAll.start();
        threadCreateDocInput.start();
        threadUpdateAll.start();
        Thread threadLog = new Thread(() -> {
            while (!Thread.interrupted()) {
                logger.info(stepController.getCountInAllStep());
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        threadLog.start();

    }
}
