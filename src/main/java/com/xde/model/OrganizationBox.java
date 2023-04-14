package com.xde.model;

import lombok.Getter;

import javax.persistence.*;

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


}
