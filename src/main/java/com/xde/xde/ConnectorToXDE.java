package com.xde.xde;

import com.xde.model.XDESettings;
import com.xde.repository.XDESettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

@Component
public class ConnectorToXDE {
    private XDESettings xdeSettings;
    @Autowired
    private XDESettingsRepository xdeSettingsRepository;

    @PostConstruct
    public void loadSettings() {
        List<XDESettings> list = xdeSettingsRepository.findAll();
        if (list.size() > 0) {
            xdeSettings = list.get(0);
        }
    }


    public List<Object> getEvents(String boxName) {
        return null;
    }

}
