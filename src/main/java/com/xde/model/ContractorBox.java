package com.xde.model;

import javax.persistence.*;

/**
 * Ящики контрагентов
 * */
@Entity
@Table(name = "contractor_box")
public class ContractorBox {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    @Column(name = "name_contractor")
    private String nameContractor;
    private String inn;
    @Column(name = "operator_id")
    private String operatorId;
    @Column(name = "fns_id")
    private String fnsId;
}
