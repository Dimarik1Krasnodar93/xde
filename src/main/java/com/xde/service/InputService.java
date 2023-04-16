package com.xde.service;

import com.xde.model.OrganizationBoxCount;
import com.xde.xde.ConnectorToXDE;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class InputService {
    private ConnectorToXDE connectorToXDE;
    private OrganizationBoxCountService organizationBoxCountService;

    public List<Map> getData(int id) {
        OrganizationBoxCount organizationBoxCount = organizationBoxCountService.findById(id);
        List<Map> list = connectorToXDE.getInputEvents(organizationBoxCount.getBox().getName());
        return list;
    }
}
