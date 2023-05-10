package com.xde.threads.runnableThreads;

import com.xde.handlers.EventHandler;
import com.xde.model.DocInput;
import com.xde.model.Event;
import com.xde.repository.DocInputRepository;
import com.xde.repository.EventRepository;
import com.xde.xde.XDEContainer;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class ThreadUpdaterStatuses extends Thread {
    private EventRepository eventRepository;
    private DocInputRepository docInputRepository;
    private int processorValue;
    private int processorsTotal;
    List<Event> listToUpdate;

    @Override
    public void run() {
        List<DocInput> listToUpdate = new ArrayList<>(this.listToUpdate.size());
        for (int i = 0; i < this.listToUpdate.size(); i++) {
            if ( i % processorsTotal == processorValue) {
                try {
                    DocInput docInput = docInputRepository.findByIdDoc(this.listToUpdate.get(i).getDocId());
                    if (docInput != null) {
                        if (EventHandler.getPriority(docInput.getStatusEd()) <
                                EventHandler.getPriority(this.listToUpdate.get(i).getStatus())) {
                            docInput.setStatusEd(this.listToUpdate.get(i).getStatus());
                            listToUpdate.add(docInput);
                        }
                    }
                } catch (Exception ex) {
                    ex.getMessage();
                }
            }
        }
        if (listToUpdate.size() > 0) {
            docInputRepository.saveAll(listToUpdate);
        }
    }
}
