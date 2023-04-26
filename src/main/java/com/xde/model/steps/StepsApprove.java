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
 * 3 - ОбменСШинойЭДОЭлектронныйДокументШагПрочитатьИзАрхива
 * 4 - Подписать на сервере xDE
 * 5 - Записать в архив
 * 6 - Завершение - операция по типу
 */
@NoArgsConstructor
public class StepsApprove implements Step {
    public static final int SECONDS_IGNORE = 15;
    public static final int TOTAL_STEPS = 6;
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
    @Column(name = "content_link")
    private String contentLink;
    @Column(name = "signature_link")
    private String signatureLink;
    @Column (name = "content_length")
    private int contentLength;
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
            case 6: return approve ? UrlQueries.getUrlDocumentsLocalAccept()
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
                String delimiter =  getDelimiter();
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("--");
                stringBuilder.append(delimiter);
                stringBuilder.append(System.lineSeparator());
                stringBuilder.append("Content-Disposition: form-data; name=\"json\"");
                stringBuilder.append(System.lineSeparator());
                stringBuilder.append(System.lineSeparator());
                stringBuilder.append(jsonObject);
                stringBuilder.append(System.lineSeparator());
                stringBuilder.append("--");
                stringBuilder.append(delimiter);
                stringBuilder.append(System.lineSeparator());
                stringBuilder.append("Content-Disposition: form-data; name=\"content\"; filename=\"");
                stringBuilder.append(event.getFileName());
                stringBuilder.append(".SGN.SGN\"");
                stringBuilder.append(System.lineSeparator());
                stringBuilder.append(System.lineSeparator());
                stringBuilder.append(result);
                stringBuilder.append(System.lineSeparator());
                stringBuilder.append("--");
                stringBuilder.append(delimiter);
                stringBuilder.append("--");
                byte[] bytes = stringBuilder.toString().getBytes();
                map.put("body", bytes);
                contentLength = bytes.length;
                break;
            case 6:
                JSONObject jsonObjectMap = new JSONObject();
                jsonObjectMap.put("DocumentId", event.getDocId());
                jsonObjectMap.put("name", event.getFileName());
                jsonObjectMap.put("ContentLinkId", contentLink);
                jsonObjectMap.put("SignatureLinkId", signatureLink);
                map.put("SignedTitlesOrReceipts", new JSONObject[] {jsonObjectMap});
        }
        return map;
    }

    private String getDelimiter() {
        return "46c46a9e342348cfad0e870ab34b81f1";
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
            case 6: return HttpMethod.POST;
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
                contentLink = result;
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
            if (step == 5) {
                result = responseEntity.getBody();
                signatureLink = result;
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
    public HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        switch (step) {
            case 3:
                headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
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
