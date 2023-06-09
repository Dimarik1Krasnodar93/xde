package com.xde.xde;

import com.sun.net.httpserver.Headers;
import com.xde.dto.TypeHttp;
import com.xde.model.Event;
import com.xde.model.XDESettings;
import com.xde.model.steps.Step;
import com.xde.repository.XDESettingsRepository;
import com.xde.service.EventService;
import lombok.Getter;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;

import java.util.*;

/**
 *Класс создает коннектор к сервису xDE.
 * Все операции с xDE выполняются через объект этого класса
 */

@Component
@Getter
 public class ConnectorToXDE {
    private int processorsCount = 600;
    private XDESettings xdeSettings;
    private String token;
    private static String bearerToken;
    private XDEContainer xdeContainer;

    private static Logger logger = LoggerFactory.getLogger(ConnectorToXDE.class);
    public static final int MAX_COUNT_EVENTS = 1500;
    private boolean fatalError = false;

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
        UrlQueries.setAllCryptoUrl(xdeSettings.getUrlCrypto());
        UrlQueries.setArchiveUrl(xdeSettings.getUrlArchive());
        UrlQueries.setUrlSign1c(xdeSettings.getUrlSign1c());
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
            try {
                Map<String, String> map = restTemplate.postForObject(UrlQueries.getUrlToken(),
                        request, HashMap.class);
                token = map.get("access_token");
                bearerToken = "Bearer " + token;
            } catch (Exception ex) {
                logger.error(ex.getMessage());
            }
        }
    }

    public Set<Map> getInputEvents(String boxId, int lastMessage) {
        Map<String, Object> map = new HashMap<>();
        map.put("BoxId", boxId);
        map.put("MaxStatusesCount", MAX_COUNT_EVENTS);
        map.put("LastStatusId", lastMessage);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", bearerToken);
        Optional<ResponseEntity<Set>> optResponseEntity = executeXdeQuery(HttpMethod.POST, headers, map,
                UrlQueries.getUrlHistory(), Set.class, String.class);
        var temp = optResponseEntity.orElse(null);
        return temp != null ? temp.getBody() : new HashSet<>();
    }

    public void executeStep(Step step) {
        synchronized (step) {
            if (!step.needToWaiting() && !step.getDone()) {
                Map<String, Object> parameters = step.getParameters();
                HttpMethod httpMethod = step.getHttpMethod();
                HttpHeaders headers = step.getHeaders();
                if (step.needAuthorization()) {
                    headers.add("Authorization", bearerToken);
                }
                Optional<ResponseEntity<String>> optResponseEntity = Optional.empty();
                try {
                    Class requestClass = parameters.containsKey("body") ? byte[].class : String.class;
                    optResponseEntity = executeXdeQuery(httpMethod, headers, parameters,
                            step.getUrlRequest(), String.class, requestClass);
                    if (!optResponseEntity.isEmpty()) {
                        if (optResponseEntity.get().getStatusCode() == HttpStatus.OK
                                && !optResponseEntity.get().getBody().contains("\"Results\":null")) {
                            step.updateResultFromResponseEntity(optResponseEntity.get());
                            step.incrementStep();
                        } else {
                            step.setError(optResponseEntity.get().getStatusCode().toString());
                            logger.error(String.format("ERROR %s step %d docId %s",
                                    optResponseEntity.get().getStatusCode(), step.getStep(), step.getEvent().getDocId()));
                        }
                    } else {
                        step.setError("empty body");
                    }
                } catch (Exception ex) {
                    logger.error(String.format("ERROR %s step %d docId %s",
                            optResponseEntity.get().getStatusCode(), step.getStep(), step.getEvent().getDocId()));
                    step.setError(String.format("%s %s", optResponseEntity.orElseThrow().getStatusCode(), ex.getMessage()));
                }
            }
        }
    }

    public <T, Y> Optional<ResponseEntity<T>> executeXdeQuery(HttpMethod httpMethod, HttpHeaders headers,
                                                              Map<String, Object> map, String urlRequest,
                                                              Class<T> classNameResponse, Class<Y> classNameRequest) {
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<Y> request;
        JSONObject jsonObject = new JSONObject();
        if (!map.isEmpty()) {
            if (map.containsKey("body")) {
                request = new HttpEntity<>((Y) map.get("body"), headers);
            } else {
                for (Map.Entry<String, Object> entry : map.entrySet()) {
                    jsonObject.put(entry.getKey(), entry.getValue());
                }
                request =
                        new HttpEntity<>((Y) jsonObject.toString(),
                                headers);
            }
        } else {
            request =
                    new HttpEntity<>(headers);
        }
        ResponseEntity<T> responseEntity = null;
        try {
            responseEntity = restTemplate.exchange(urlRequest, httpMethod, request, classNameResponse);
        } catch (Exception ex) {
            logger.error(ex.getMessage() + " request " + urlRequest);
            if (responseEntity == null) {
                if (!ex.getMessage().contains("запланированные задачи")
                && !ex.getMessage().contains("недоступна")) {
                   // fatalError = true;
                    logger.error("--fatal error--" + ex.getMessage());
                }
            }
            return Optional.empty();
        }
        return Optional.of(responseEntity);
    }

    public String getEventArchive(Event event) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        Optional<ResponseEntity<String>> result = executeXdeQuery(HttpMethod.GET, headers, new HashMap<>(),
                UrlQueries.getUrlGetArchive() + event.getLinkArchive(), String.class, String.class);
        String data =  result.orElseThrow().getBody();
        return data;
    }
}
