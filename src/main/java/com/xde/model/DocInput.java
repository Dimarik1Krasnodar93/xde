package com.xde.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Entity
@Setter
@Table(name = "doc_input")
public class DocInput {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "id_doc")
    private String idDoc;
    @Column(name = "id_pac")
    private String idPac;
    @Column(name = "id_box_contractor")
    private String idBoxContractor;
    @Column(name = "id_box")
    private String idBox;
    @Column(name = "status_ed")
    private String statusEd;
    @Column(name = "status_edo")
    private String statusEdo;
    @Column(name = "kind_doc")
    private String kindDoc;
    @Column(name = "date_doc")
    private LocalDateTime dateDoc;
    @Column(name = "date_sign")
    private LocalDateTime dateSign;
    @Column(name = "date_get")
    private LocalDateTime dateGet;
    @Column(name = "date_agreement")
    private LocalDateTime dateAgreement;
    @Column(name = "fns_contractor")
    private String fnsContractor;
    @Column(name = "fns_organization")
    private String fnsOrganization;
    @Column(name = "contractor_guid")
    private String contractorGuid;
    @Column(name = "contractor_inn")
    private String contractorInn;
    @Column(name = "contractor_kpp")
    private String contractorKpp;
    @Column(name = "number_doc")
    private String numberDoc;
    private double sum;
    @Column(name = "type_doc")
    private int typeDoc;
    @Column(name = "need_alert_get")
    private boolean needAlertGet;
    @Column(name = "need_sign")
    private boolean needSign;
    @Column(name = "flag_error")
    private boolean flagError;
    @Column(name = "organization_guid")
    private String organizationGuid;



}
