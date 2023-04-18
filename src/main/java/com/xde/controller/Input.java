package com.xde.controller;

import com.xde.service.InputService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Контроллеры для получения входящих данных из xDE
 */
@RestController
@RequestMapping("/input")
@AllArgsConstructor
public class Input {
    InputService inputService;
    @GetMapping("/getData/{id}")
    public Object getData(@PathVariable("id") int id) {
        List<Map> list = inputService.getEvents(id);
        return list;
    }

}
