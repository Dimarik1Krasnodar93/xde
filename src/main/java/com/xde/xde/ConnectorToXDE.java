package com.xde.xde;

import com.xde.model.XDESettings;
import com.xde.repository.XDESettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 *Класс создает коннектор к сервису xDE
 */
public
@Component
 class ConnectorToXDE {
    private XDESettings xdeSettings;
    @Autowired
    private XDESettingsRepository xdeSettingsRepository;

    /**
     * Загрузка настроек из БД
    * */
    @PostConstruct
    public void loadSettings() {
        List<XDESettings> list = xdeSettingsRepository.findAll();
        if (list.size() > 0) {
            xdeSettings = list.get(0);
        }
    }

    /**
     *Получение входяших событий из xDE
     */
    public List<Object> getEvents(String boxName) {
        return null;
    }

}
