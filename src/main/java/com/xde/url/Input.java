package com.xde.url;

import com.xde.service.InputService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;

@RestController
@RequestMapping("/input")
@AllArgsConstructor
public class Input {
    InputService inputService;
    @PostMapping("/getData/{id}")
    public Object getData(@PathVariable("id") int id){
        inputService.getData(id);
        return "";
    }

}
