package com.xde.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * Настройки xDE
 * */
@Entity
@Table(name = "xde_settings")
@Getter
@Setter
public class XDESettings {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String url;
    private String login;
    private String password;
}
