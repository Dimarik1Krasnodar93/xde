package com.xde.threads.runnableThreads;

import com.xde.dto.StepResult;
import com.xde.enums.StepType;
import com.xde.model.DocInput;
import com.xde.model.steps.Step;
import com.xde.repository.DocInputRepository;
import com.xde.xde.XDEContainer;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@AllArgsConstructor
public class ThreadSaverResults extends Thread {
    private XDEContainer xdeContainer;
    private int processorValue;

    private DocInputRepository docInputRepository;



    @Override
    public void run() {
        List<Step> steps = xdeContainer.getMap()
                .get(StepType.CREATE_INPUT).get(processorValue);
        List<DocInput> list = new ArrayList<>();
        List<Step> stepsSaved = new LinkedList<>();
        for (Step step : steps) {
            if (step.getDone() && step.needToSave() && !step.getSavedResults()) {
                StepResult stepResult = step.getStepResult();
                DocInput docInput = stepResult.getDocInput();
                list.add(docInput);
                stepsSaved.add(step);
            }
        }
        if (list.size() > 0) {
            docInputRepository.saveAll(list);
        }
        for (Step step : stepsSaved) {
            step.setSavedResults();
        }
    }
}
