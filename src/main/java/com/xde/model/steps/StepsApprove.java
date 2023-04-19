package com.xde.model.steps;

import com.xde.model.Event;
import com.xde.model.OrganizationBox;
import com.xde.xde.ConnectorToXDE;
import com.xde.xde.UrlQueries;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
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
        if (step == 1) {
            return approve ? UrlQueries.getUrlGetTitleOrReceiptAccept()
                    : UrlQueries.getUrlGetTitleOrReceiptReject();
        } else {
            return "";
        }
    }

    @Override
    public void incrementStep() {
        if (++step > TOTAL_STEPS) {
            done = true;
        }
    }

    public Map<String, Object> getParameters() {
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> acceptanceResult = new HashMap<>();
        acceptanceResult.put("AcceptanceResultType", 1);
        acceptanceResult.put("DocumentId", event.getDocId());
        map.put("AcceptanceResult", acceptanceResult);
        map.put("DocumentId", event.getDocId());
        OrganizationBox organizationBox = event.getOrganizationBox();
        map.put("Certificate", organizationBox.getCertificate());
        map.put("Thumbprint", organizationBox.getThumbprint());
        return map;
    }

    @Override
    public boolean needToWaiting() {
        return LocalDateTime.now()
                .isBefore(lastXdeTime.plusSeconds(SECONDS_IGNORE));
    }

    @Override
    public boolean getDone() {
        return done;
    }
}
