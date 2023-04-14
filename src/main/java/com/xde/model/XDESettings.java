package com.xde.model;

import javax.persistence.*;

@Entity
@Table(name = "xde_settings")
public class XDESettings {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String url;
    private String login;
    private String password;
}
