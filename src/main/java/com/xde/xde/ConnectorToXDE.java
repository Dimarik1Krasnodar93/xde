package com.xde.xde;

import com.xde.dto.TypeHttp;
import com.xde.model.XDESettings;
import com.xde.model.steps.Step;
import com.xde.repository.XDESettingsRepository;
import lombok.Getter;
import org.json.JSONObject;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *Класс создает коннектор к сервису xDE.
 * Все операции с xDE выполняются через объект этого класса
 */

@Component
@Getter
 public class ConnectorToXDE {
    private int processorsCount = 1;
    private XDESettings xdeSettings;
    private String token;
    private static String bearerToken;
    private XDEContainer xdeContainer;

    public static final int MAX_COUNT_EVENTS = 100;

    private XDESettingsRepository xdeSettingsRepository;

    public ConnectorToXDE(XDESettingsRepository xdeSettingsRepository) {
        this.xdeSettingsRepository = xdeSettingsRepository;
        xdeContainer = new XDEContainer(processorsCount, this);
    }

    /**
     * Загрузка настроек из БД
    * */
    @PostConstruct
    public void loadSettings() {
        List<XDESettings> list = xdeSettingsRepository.findAll();
        if (list.size() > 0) {
            xdeSettings = list.get(0);
        }
        loadPaths();
        updateToken(true);
    }

    private void loadPaths() {
        UrlQueries.setAllUrl(xdeSettings.getUrl());
    }


    /**
     * Метод для обновления токена из xDE
     * @param needToUpdate - необходимость обновления токена
     */
    private void updateToken(boolean needToUpdate) {
        if (needToUpdate || token == null) {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Username", xdeSettings.getLogin());
            jsonObject.put("Password", xdeSettings.getPassword());
            HttpEntity<String> request =
                    new HttpEntity<>(jsonObject.toString(),
                            headers);
            Map<String, String> map = restTemplate.postForObject(UrlQueries.getUrlToken(),
                    request, HashMap.class);
            token = map.get("access_token");
            bearerToken = "Bearer " + token;
        }
    }

    public Set<Map> getInputEvents(String boxId, int lastMessage) {
        Map<String, Object> map = new HashMap<>();
        map.put("BoxId", boxId);
        map.put("MaxStatusesCount", MAX_COUNT_EVENTS);
        map.put("LastStatusId", lastMessage);
        ResponseEntity<Set> responseEntity = executeXdeQuery(HttpMethod.POST, map, UrlQueries.getUrlHistory(), Set.class);
        return (Set<Map>) responseEntity.getBody();
    }

    public void executeStep(Step step) {
        if (!step.needToWaiting()) {
            Map<String, Object> parameters = step.getParameters();
            HttpMethod httpMethod = step.getHttpMethod();
            ResponseEntity<String> responseEntity = executeXdeQuery(httpMethod, parameters,
                    step.getUrlRequest(), String.class);
            step.updateResultFromResponseEntity(responseEntity);
            step.incrementStep();
        }
    }
    public <T> ResponseEntity<T>  executeXdeQuery(HttpMethod httpMethod, Map<String, Object> map, String urlRequest, Class<T> className) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", bearerToken);
        HttpEntity<String> request;
        JSONObject jsonObject = new JSONObject();
        if (!map.isEmpty()) {
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                jsonObject.put(entry.getKey(), entry.getValue());
            }
            request =
                    new HttpEntity<>(jsonObject.toString(),
                            headers);
        } else {
            request =
                    new HttpEntity<>(headers);
        }
        ResponseEntity<T> responseEntity = restTemplate.exchange(urlRequest, httpMethod, request, className);
        return responseEntity;
    }

    /**
     *Получение входяших событий из xDE
     */
    public List<Object> getEvents(String boxName) {
        return null;
    }

}
