package com.xde.threads.runnableThreads;

import com.xde.dto.StepResult;
import com.xde.enums.StepType;
import com.xde.model.DocInput;
import com.xde.model.Event;
import com.xde.model.steps.Step;
import com.xde.repository.DocInputRepository;
import com.xde.xde.XDEContainer;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

@AllArgsConstructor
public class ThreadSaverResults extends Thread {
    private XDEContainer xdeContainer;
    private int processorValue;

    private DocInputRepository docInputRepository;



    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
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
            //должно быть в 1 строчку
            if (list.size() > 0) {
                //отладка временно. задвоение
                List<String> listString = new ArrayList<>(list.size());
                for (DocInput docInput : list) {
                    listString.add(docInput.getIdDoc());
                }
                List<DocInput> listInDb = docInputRepository.findByIdDocIn(listString);
                Iterator<DocInput> iterator = list.iterator();
                List<DocInput> listToCreate = new ArrayList<>(list.size());
                //docInputRepository.saveAll(list);
                for (DocInput docInput : list) {
                    try {
                        docInputRepository.save(docInput);
                    } catch (Exception ex) {
                        ex.getMessage();
                    }
                }
                boolean skipElement = false;
                for (DocInput docList : list) {
                    skipElement = false;
                    for (DocInput docInput : listInDb) {
                        if (docList.getIdDoc().equals(docInput.getIdDoc())) {
                            skipElement = true;
                            break;
                        }
                        //отладка
                        else {
                            int e = 5;
                            continue;
                        }
                    }
                    if (!skipElement) {
                        listToCreate.add(docList);
                    }
                }
            }
            for (Step step : stepsSaved) {
                step.setSavedResults();
            }
            Thread.currentThread().interrupt();
        }
    }
}
