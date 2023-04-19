package com.xde.model.steps;

import com.xde.model.OrganizationBox;

import javax.persistence.*;

@Entity
@Table(name = "title_or_receipt")
public class TitleOrReceipt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @ManyToOne
    @JoinColumn(name = "organization_box_id")
    private OrganizationBox organizationBox;

    //private
}
