package com.xde.model;

import lombok.Getter;
import org.hibernate.annotations.Type;

import javax.persistence.*;

/**
 * Ящики организаций
 * */
@Entity
@Table(name = "organization_box")
@Getter
public class OrganizationBox {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    @Column(name = "name_org")
    private String nameOrg;
    private String inn;
    @Column(name = "operator_id")
    private String operatorId;
    @Column(name = "fns_id")
    private String fnsId;
    @Column
    private String thumbprint;
    @Column(name = "thumbprint_server")
    private String thumbprintServer;
    @Column
    @Type(type = "org.hibernate.type.TextType")
    private String certificate;
}
