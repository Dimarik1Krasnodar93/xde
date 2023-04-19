package com.xde.model.steps;

import java.util.Map;

public interface Step {
    Map<String, Object> getParameters();
    boolean needToWaiting();
    String getUrlRequest();
    void incrementStep();
    boolean getDone();
}
