package com.xde.service;

import com.xde.model.OrganizationBoxCount;
import com.xde.xde.ConnectorToXDE;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class InputService {
    private ConnectorToXDE connectorToXDE;
    private OrganizationBoxCountService organizationBoxCountService;

    public Object getData(int id) {
        OrganizationBoxCount organizationBoxCount = organizationBoxCountService.findById(id);
        connectorToXDE.getEvents(organizationBoxCount.getBox().getName());
        return new Object();
    }
}
