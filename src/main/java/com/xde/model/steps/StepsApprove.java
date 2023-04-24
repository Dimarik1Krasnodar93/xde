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
 * 3 - ОбменСШинойЭДОЭлектронныйДокументШагПрочитатьИзАрхива
 * 4 - Подписать на сервере xDE
 * 5 - Записать в архив
 */
@NoArgsConstructor
public class StepsApprove implements Step {
    public static final int SECONDS_IGNORE = 1;
    public static final int TOTAL_STEPS = 5;
    public static final int ATTACHMENT_SIGN = 14;

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
            case 3: return UrlQueries.getUrlGetArchive() + result;
            case 4: return UrlQueries.getUrlSign();
            case 5: return UrlQueries.getUrlGetArchive();
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
            case 4:
                map.put("Thumbprint", organizationBox.getThumbprintServer());
                map.put("ThrowOnErrors", true);
                map.put("Contents", new String[]{result}); //ДанныеНаПодписаниеМассив.Добавить(Base64Строка(ДанныеДляПодписания));
                break;
            case 5:
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("FileName", event.getFileName() + ".SGN");
                jsonObject.put("AttachmentType", ATTACHMENT_SIGN);
                jsonObject.put("DocumentId", event.getDocId());
                String delimiter =  "--" + event.getDocId() + event.getEventId() + "--";
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(delimiter);
                stringBuilder.append(System.lineSeparator());
                stringBuilder.append("Content-Disposition: form-data; name=\"json\"");
                stringBuilder.append(System.lineSeparator());
                stringBuilder.append(System.lineSeparator());
                stringBuilder.append(jsonObject);
                stringBuilder.append(System.lineSeparator());
                stringBuilder.append(delimiter);
                stringBuilder.append(System.lineSeparator());
                stringBuilder.append("Content-Disposition: form-data; name=\"content\"; filename=\"");
                stringBuilder.append(event.getFileName());
                stringBuilder.append(".SGN\"");
                stringBuilder.append(System.lineSeparator());
                stringBuilder.append(result);
                stringBuilder.append(System.lineSeparator());
                stringBuilder.append(delimiter);
                map.put("body", stringBuilder.toString());

                break;
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
            case 3: return HttpMethod.GET;
            case 4: return HttpMethod.POST;
            case 5: return HttpMethod.POST;
            default: return HttpMethod.POST;
        }
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
                result = (String) ((JSONObject)((JSONArray)jsonObjectEntity.get("Results")).get(0)).get("ContentLinkId");
            }
            if (step == 3) {
                sign = responseEntity.getBody();
                result = Base64.getEncoder().encodeToString(sign.getBytes());
            }
            if (step == 4) {
                result = responseEntity.getBody();
                result = result.substring(1);
                result = result.substring(0, result.length() - 1);
                JSONObject jsonObject = new JSONObject(result);
                result = (String) jsonObject.get("Result");
                result = new String(Base64.getDecoder().decode(result));
            }
        } else {
            fatalException = true;
            exceptionMessage = responseEntity.getBody();
        }
    }

    @Override
    public MediaType getContentType() {

        switch (step) {
            case 3: return MediaType.APPLICATION_OCTET_STREAM;
            default: return MediaType.APPLICATION_JSON;
        }
    }

    @Override
    public void setError(String message) {
        fatalException = true;
        exceptionMessage = message;
        lastXdeTime = LocalDateTime.now();
    }
}
