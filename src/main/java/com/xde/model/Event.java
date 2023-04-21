package com.xde.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * События (пример - в 1с это входящие события)
 * */
@Getter
@Setter
@Entity
@Table
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @ManyToOne
    @JoinColumn(name = "organization_box")
    private OrganizationBox organizationBox;
    @Column(name = "doc_id")
    private String docId;
    private String status;
    @Column(name = "date_time")
    private LocalDateTime dateTime;
    @Column(name = "print_form")
    private boolean printForm;
    @Column(name = "event_id")
    private int eventId;
    @Column(name = "code_event")
    private String codeEvent;
    @Column(name = "output_doc")
    private boolean outputDoc;
    @Column(name = "link_archive")
    private String linkArchive;
    @Column(name = "type_event")
    private int typeEvent;
    @Column(name = "file_name")
    private String fileName;
    @Column(name = "certificate_sfm")
    private String certificateSFM;
    @Column(name = "certificate_date_from")
    private LocalDateTime certificateDateFrom;
    @Column(name = "certificate_date_to")
    private LocalDateTime certificateDateTo;
    @Column(name = "certificate_Number")
    private String certificateNumber;
    @Column(name = "certificate_thumbprint")
    private String certificateThumbprint;
    @Column(name = "warrant_proxy")
    private String warrantProxy;
    @Column(name = "started_execution")
    private boolean startedExecution;

    private byte[] data;

    public boolean getPrintForm() {
        return printForm;
    }

}
