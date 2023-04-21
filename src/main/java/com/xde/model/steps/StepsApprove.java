package com.xde.model.steps;

import com.xde.dto.TypeHttp;
import com.xde.model.Event;
import com.xde.model.OrganizationBox;
import com.xde.xde.ConnectorToXDE;
import com.xde.xde.UrlQueries;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import javax.persistence.*;
import javax.print.attribute.standard.Media;
import java.time.LocalDateTime;
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
 * 3 - ОбменСШинойЭДОЭлектронныйДокументШагПрочитатьИзАрхива
 * 4 -
 */
@NoArgsConstructor
public class StepsApprove implements Step {
    public static final int SECONDS_IGNORE = 20;
    static final int TOTAL_STEPS = 3;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;
    private boolean approve;
    private int step;
    private String result;

    private boolean fatalException;
    private String exceptionMessage;
    private boolean done;
    private LocalDateTime lastXdeTime = LocalDateTime.now().minusSeconds(SECONDS_IGNORE);


    public StepsApprove(boolean approve, Event event) {
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
            default: return "";
        }

    }

    @Override
    public void incrementStep() {
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
                map.put("Thumbprint", organizationBox.getThumbprint());
                map.put("ThrowOnErrors", true);
                map.put("Contents", null); //ДанныеНаПодписаниеМассив.Добавить(Base64Строка(ДанныеДляПодписания));
        }
        return map;
    }

    @Override
    public boolean needToWaiting() {
        return LocalDateTime.now()
                .isBefore(lastXdeTime.plusSeconds(SECONDS_IGNORE));
    }

    @Override
    public HttpMethod getHttpMethod() {
        switch (step) {
            case 1: return HttpMethod.POST;
            case 2: return HttpMethod.GET;
            default: return HttpMethod.POST;
        }
    }
    @Override
    public boolean getDone() {
        return done;
    }

    @Override
    public void updateResultFromResponseEntity(ResponseEntity<String> responseEntity) {
        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            if (step == 1) {
                result = responseEntity.getBody();
                fatalException = false;
                exceptionMessage = "";
            }
            if (step == 2) {
                JSONObject jsonObjectEntity = new JSONObject(responseEntity.getBody());
                result = "";
                result = (String) ((JSONObject)((JSONArray)jsonObjectEntity.get("Results")).get(0)).get("ContentLinkId");
            }
        } else {
            fatalException = true;
            exceptionMessage = responseEntity.getBody();
        }
    }

    @Override
    public MediaType getContentType() {
        return MediaType.APPLICATION_JSON;
    }
}
