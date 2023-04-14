package com.xde.model;

import javax.persistence.*;

@Entity
@Table (name = ("organization_box_count"))
public class OrganizationBoxCount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToOne
    @JoinColumn(name = "box_id")
    private OrganizationBox box;
    private int count;

}
