package com.xde.model.steps;

import com.xde.dto.StepResult;
import com.xde.model.DocInput;
import com.xde.model.Event;
import com.xde.xde.UrlQueries;
import lombok.Getter;
import lombok.Setter;
import org.json.JSONObject;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Шаги по созданию входящего документа
 * */
@Entity
@Table(name = "steps_create_doc_input")
@Getter
@Setter
public class StepCreateDocInput implements Step {

    public static final int SECONDS_IGNORE = 1;
    public static final int TOTAL_STEPS = 1;

    private boolean fatalException;
    private int step;
    private String exceptionMessage;
    private boolean done;

    private boolean savedResults;
    @Transient
    private StepResult stepResult;
    private LocalDateTime lastXdeTime = LocalDateTime.now().minusSeconds(SECONDS_IGNORE);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;
    private String result;
    public StepCreateDocInput(Event event) {
        this.event = event;
        step = 1;
    }

    public StepCreateDocInput() {

    }

    @Override
    public Map<String, Object> getParameters() {
        return new HashMap<>();
    }

    @Override
    public boolean needToWaiting() {
        return fatalException ? LocalDateTime.now()
                .isBefore(lastXdeTime.plusSeconds( 2 * SECONDS_IGNORE)) : LocalDateTime.now()
                .isBefore(lastXdeTime.plusSeconds(SECONDS_IGNORE)) ;
    }

    @Override
    public String getUrlRequest() {
        return UrlQueries.getUrlMetaData() + event.getDocId();
    }

    @Override
    public void incrementStep() {
        lastXdeTime = LocalDateTime.now();
        if (!fatalException && ++step > TOTAL_STEPS) {
            done = true;
        }
    }

    @Override
    public boolean getDone() {
        return done;
    }

    @Override
    public boolean getSavedResults() {
        return savedResults;
    }

    @Override
    public HttpMethod getHttpMethod() {
        return HttpMethod.GET;
    }

    @Override
    public HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    @Override
    public void setError(String message) {
        fatalException = true;
        exceptionMessage = message;
        lastXdeTime = LocalDateTime.now();
    }

    @Override
    public void updateResultFromResponseEntity(ResponseEntity<String> responseEntity) {
        result = responseEntity.getBody();
    }

    @Override
    public StepResult getStepResult() {
        stepResult = new StepResult();
        JSONObject jsonObject = new JSONObject(result);
        try {

            DocInput docInput = new DocInput();
            docInput.setIdDoc(event.getDocId());
            docInput.setIdBoxContractor((String) jsonObject.get("SenderBoxId"));
            docInput.setStatusEd(event.getStatus());
            docInput.setDateDoc(LocalDateTime.parse((String) jsonObject.get("OperatorDateTime")));
            docInput.setNumberDoc((String) jsonObject.get("Number"));
            docInput.setIdBox(event.getOrganizationBox().getName());
            docInput.setTypeDoc((Integer) jsonObject.get("Type"));
            Object total = jsonObject.get("Total");
            if (total != JSONObject.NULL)  {
                docInput.setSum((Double) total);
            }
            stepResult.setDocInput(docInput);
        } catch (Exception ex) {
            ex.getMessage();
        }


        return stepResult;
    }

    @Override
    public int getStep() {
        return step;
    }

    @Override
    public boolean needToSave() {
        return true;
    }

    @Override
    public Event getEvent() {
        return event;
    }

    @Override
    public boolean needAuthorization() {
        return true;
    }

    @Override
    public void setSavedResults() {
        savedResults = true;
    }

}
