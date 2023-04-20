package com.xde.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * web-интерфейс для отладки http://localhost:8080/index

* */
@Controller
public class IndexController {
    @GetMapping("/index")
    public String getIndex() {
        return "index";
    }
}
