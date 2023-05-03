package com.xde.controller;

import com.xde.model.steps.Step;
import com.xde.model.steps.StepsApprove;
import com.xde.xde.ConnectorToXDE;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

/**
 * Контроллер для вывода информации по шагам
 * */
@AllArgsConstructor
@RestController
@RequestMapping("/step")
public class StepController {
    ConnectorToXDE connectorToXDE;
    @GetMapping("/getByDocId/{docId}")
    public Step getByDocId(@PathVariable("docId") String docId) {
        return connectorToXDE.getXdeContainer().getMap()
                .values()
                .parallelStream()
                .flatMap( i -> i.parallelStream())
                .filter(i -> docId.equals(i.getEvent().getDocId()))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException(String.format("Not found by docId %s", docId)));
    }

    @GetMapping("/getInStep/{step}")
    public List<Step> getInStep(@PathVariable("step") int step) {
        return connectorToXDE.getXdeContainer().getMap()
                .values()
                .parallelStream()
                .flatMap( i -> i.parallelStream())
                .filter(i -> i.getStep() == step)
                .collect(Collectors.toList());
    }

    @GetMapping("/getCountInStep/{step}")
    public long getCountInStep(@PathVariable("step") int step) {
        return connectorToXDE.getXdeContainer().getMap()
                .values()
                .parallelStream()
                .flatMap( i -> i.parallelStream())
                .filter(i -> i.getStep() == step)
                .count();
    }

    @ExceptionHandler
    public ResponseEntity<String> handleException(NoSuchElementException noSuchElementException) {
        return ResponseEntity.badRequest().body(noSuchElementException.getMessage());
    }
}