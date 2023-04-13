package com.xde.model;

import javax.persistence.*;

@Entity
@Table(name = "organization_box"
)
public class OrganizationBox {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    private String inn;
    @Column(name = "operator_id")
    private String operatorId;
    @Column(name = "fns_id")
    private String fnsId;


}
