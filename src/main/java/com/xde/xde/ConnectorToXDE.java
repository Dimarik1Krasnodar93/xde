package com.xde.xde;

import com.xde.httentity.Userxde;
import com.xde.model.XDESettings;
import com.xde.repository.XDESettingsRepository;
import lombok.Getter;
import org.apache.catalina.User;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *Класс создает коннектор к сервису xDE
 */

@Component
@Getter
 public class ConnectorToXDE {
    private XDESettings xdeSettings;
    private String token;
    private String bearerToken;

    private String urlToken;
    private String urlHistory;
    private final int maxCountEvents = 100;
   // public static final int TIMEOUT = 200;

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
        loadPaths();
        updateToken(true);
        getInputEvents("2");
    }

    private void loadPaths() {
        urlToken = xdeSettings.getUrl() + "/token";
        urlHistory = xdeSettings.getUrl() + "/v3/statuses/history";
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
            Map<String, String> map = restTemplate.postForObject(urlToken,
                    request, HashMap.class);
            token = map.get("access_token");
            bearerToken = "Bearer " + token;
        }
    }

    public List<Map> getInputEvents(String boxId) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", bearerToken);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("BoxId", "a3893aa4b53f498da38e440895bf4361@diadoc.ru");
        jsonObject.put("MaxStatusesCount", 1000);
        jsonObject.put("LastStatusId", 37798);
        HttpEntity<String> request =
                new HttpEntity<>(jsonObject.toString(),
                        headers);
        return restTemplate.postForObject(urlHistory,
                request, List.class);
    }

    /**
     *Получение входяших событий из xDE
     */
    public List<Object> getEvents(String boxName) {
        return null;
    }

}
