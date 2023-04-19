package com.xde.controller;

import com.xde.service.EventService;
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
        return eventService.getEvents(id);
    }
    @PostMapping("/approve")
    public void approve() {
            eventService.approveAll();
    }

}
