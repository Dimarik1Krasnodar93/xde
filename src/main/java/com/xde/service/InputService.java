package com.xde.service;

import com.xde.model.OrganizationBoxCount;
import com.xde.xde.ConnectorToXDE;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class InputService {
    private ConnectorToXDE connectorToXDE;
    private OrganizationBoxCountService organizationBoxCountService;
    private EventService eventService;


}
