package com.xde.controller;

import com.xde.service.EventService;
import com.xde.threads.runnableThreads.ThreadApproveAll;
import com.xde.threads.runnableThreads.ThreadGetEvents;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * Контроллеры для получения входящих данных из xDE
 */
@RestController
@RequestMapping("/event")
@AllArgsConstructor
public class EventController {
    EventService eventService;
    @PostMapping("/getEvents/{id}")
    public Object getData(@PathVariable("id") int id) {
        eventService.getEvents(id);
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
        threadGetEvents.start();
        threadApproveAll.start();
    }
}
