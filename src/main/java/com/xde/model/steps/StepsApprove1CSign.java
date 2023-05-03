package com.xde.model.steps;

import com.xde.model.Event;
import com.xde.model.OrganizationBox;
import com.xde.xde.UrlQueries;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "steps_approve")
@Getter
@Setter
/**
 * Шаги по принятию/отклонению документа
 * @approve - принять/отклонить документ
 * @step - номер шага :
 * 1 - ОбменСШинойЭДОЭлектронныйДокументШагСгенерироватьТитулИлиКвитанцию
 * 2 - ОбменСШинойЭДОЭлектронныйДокументШагПолучитьСсылкуНаКонтент
 * 3 - 1С-sign
 */
@NoArgsConstructor
public class StepsApprove1CSign implements Step {
    public static final int SECONDS_IGNORE = 5;
    public static final int TOTAL_STEPS = 4;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;
    private boolean approve;
    private int step;
    private String result;
    private String sign;
    @Column(name = "content_link")
    private String contentLink;
    @Column(name = "signature_link")
    private String signatureLink;
    @Column (name = "content_length")
    private int contentLength;
    private String name;
    private boolean fatalException;
    private String exceptionMessage;
    private boolean done;
    private LocalDateTime lastXdeTime = LocalDateTime.now().minusSeconds(SECONDS_IGNORE);


    public StepsApprove1CSign(boolean approve, Event event) {
        this.event = event;
        this.approve = approve;
        step = 1;
    }


    public String getUrlRequest() {
        switch (step) {
            case 1:
                return approve ? UrlQueries.getUrlGetTitleOrReceiptAccept()
                        : UrlQueries.getUrlGetTitleOrReceiptReject();
            case 2: return approve ? UrlQueries.getUrlGetLinkForContentAccept() + result
                    : UrlQueries.getUrlGetLinkForContentReject() + result;
            case 3: return UrlQueries.getUrlSign1c();
            case 4: return approve ? UrlQueries.getUrlDocumentsLocalAccept()
                    : UrlQueries.getUrlDocumentsLocalReject();
            default: return "";
        }

    }

    @Override
    public void incrementStep() {
        lastXdeTime = LocalDateTime.now();
        if (!fatalException && ++step > TOTAL_STEPS) {
            done = true;
        }
    }

    public Map<String, Object> getParameters() {
        OrganizationBox organizationBox = event.getOrganizationBox();
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> acceptanceResult = new HashMap<>();
        switch (step) {
        case 1:
            acceptanceResult.put("AcceptanceResultType", 1);
            acceptanceResult.put("DocumentId", event.getDocId());
            map.put("AcceptanceResult", acceptanceResult);
            map.put("DocumentId", event.getDocId());

            map.put("Certificate", organizationBox.getCertificate());
            map.put("Thumbprint", organizationBox.getThumbprint());
        break;
            case 2: break;
            case 3:
                map.put("archLink", result);
                map.put("thumbprint", event.getOrganizationBox().getThumbprint1C());
                map.put("сertPassword", event.getOrganizationBox().getCertificatePassword1C());
                map.put("archType", "3");
                map.put("DocumentId", event.getDocId());
                break;
            case 4:
                JSONObject jsonObjectMap = new JSONObject();
                jsonObjectMap.put("DocumentId", event.getDocId());
                jsonObjectMap.put("name", name);
                jsonObjectMap.put("ContentLinkId", contentLink);
                jsonObjectMap.put("SignatureLinkId", result);
                map.put("SignedTitlesOrReceipts", new JSONObject[] {jsonObjectMap});

        }
        return map;
    }

    private String getDelimiter() {
        return "46c46a9e342348cfad0e870ab34b81f1";
    }
    @Override
    public boolean needToWaiting() {
        return fatalException ? LocalDateTime.now()
                .isBefore(lastXdeTime.plusSeconds( 2 * SECONDS_IGNORE)) : LocalDateTime.now()
                .isBefore(lastXdeTime.plusSeconds(step != 2 ? SECONDS_IGNORE : 2 * SECONDS_IGNORE)) ;
    }

    @Override
    public HttpMethod getHttpMethod() {
        switch (step) {
            case 1: return HttpMethod.POST;
            case 2: return HttpMethod.GET;
            case 3: return HttpMethod.POST;
            case 4: return HttpMethod.POST;
            default: return HttpMethod.POST;
        }
    }
    @Override
    public boolean needAuthorization() {
        return step == 3 ? false : true;
    }
    @Override
    public boolean getDone() {
        return done;
    }

    @Override
    public void updateResultFromResponseEntity(ResponseEntity<String> responseEntity) {
        fatalException = false;
        exceptionMessage = "";
        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            if (step == 1) {
                result = responseEntity.getBody();
            }
            if (step == 2) {
                JSONObject jsonObjectEntity = new JSONObject(responseEntity.getBody());
                result = "";
                JSONObject jsonResults = ((JSONObject)((JSONArray)jsonObjectEntity.get("Results")).get(0));
                result = (String) jsonResults.get("ContentLinkId");
                name = (String) jsonResults.get("Name");
                contentLink = result;
            }
            if (step == 3) {
                result = responseEntity.getBody();
                JSONObject jsonObject = new JSONObject(responseEntity.getBody());
                result = (String) jsonObject.get("archLinkSIGN");
            }
        } else {
            fatalException = true;
            exceptionMessage = responseEntity.getBody();
        }
    }



    @Override
    public HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        switch (step) {
            case 3:
                headers.setContentType(MediaType.APPLICATION_JSON);
                break;
            case 4:
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.add("Accept", MediaType.APPLICATION_JSON.toString());
                break;
            case 5:
                headers.add("accept", "text/plain");
                headers.add("Content-Type", "multipart/form-data; boundary=" + getDelimiter());
                headers.add("Content-Length", String.valueOf(contentLength));
                break;
            default:
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.add("Accept", MediaType.APPLICATION_JSON.toString());
        }
        return headers;
    }

    @Override
    public void setError(String message) {
        fatalException = true;
        exceptionMessage = message;
        lastXdeTime = LocalDateTime.now();
    }
}
