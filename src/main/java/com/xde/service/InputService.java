package com.xde.service;

import com.xde.model.OrganizationBoxCount;
import com.xde.xde.ConnectorToXDE;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class InputService {
    private ConnectorToXDE connectorToXDE;
    private OrganizationBoxCountService organizationBoxCountService;
    private EventService eventService;

    public List<Map> getEvents(int id) {

        OrganizationBoxCount organizationBoxCount = organizationBoxCountService.findById(id);
        String boxId = organizationBoxCount.getBox().getName();
        int lastMessage = organizationBoxCount.getCount();
        List<Map> list = connectorToXDE.getInputEvents(boxId, lastMessage);
        eventService.saveAllFromXDE(list, boxId);
        return list;
    }
}
