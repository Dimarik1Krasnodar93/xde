package com.xde.model.steps;

import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.util.Map;

/**Шаги задач
 * */
public interface Step {
    Map<String, Object> getParameters();
    boolean needToWaiting();
    String getUrlRequest();
    void incrementStep();
    boolean getDone();
    HttpMethod getHttpMethod();


    void updateResultFromResponseEntity(ResponseEntity<String> responseEntity);
}
