package com.xde;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories
public class XDeApplication {

    public static void main(String[] args) {
        SpringApplication.run(XDeApplication.class, args);
    }

}
