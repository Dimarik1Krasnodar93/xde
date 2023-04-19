package com.xde.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table (name = ("organization_box_count"))
@Getter
public class OrganizationBoxCount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToOne
    @JoinColumn(name = "box_id")
    private OrganizationBox box;
    @Setter
    private int count;

}
