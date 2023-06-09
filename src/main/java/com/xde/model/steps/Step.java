package com.xde.model.steps;

import com.xde.dto.StepResult;
import com.xde.model.Event;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
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
    boolean getSavedResults();
    void setSavedResults();
    HttpMethod getHttpMethod();
    HttpHeaders getHeaders();
    void setError(String message);
    void updateResultFromResponseEntity(ResponseEntity<String> responseEntity);
    int getStep();
    boolean needToSave();
    Event getEvent();
    boolean needAuthorization();
    StepResult getStepResult();
}
