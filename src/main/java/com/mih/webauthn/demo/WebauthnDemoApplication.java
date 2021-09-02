package com.mih.webauthn.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.Repository;

@SpringBootApplication
@EnableJpaRepositories
@EntityScan("com.mih.webauthn.demo")
public class WebauthnDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebauthnDemoApplication.class, args);
    }
}
