package com.mih.webauthn.demo.data;

import com.mih.webauthn.domain.WebAuthnUser;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity
public class MyUser implements WebAuthnUser {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String username;
    private byte[] recoveryToken;
    private byte[] addToken;
    private LocalDateTime registrationAddStart;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public byte[] getRecoveryToken() {
        return recoveryToken;
    }

    @Override
    public void setRecoveryToken(byte[] recoveryToken) {
        this.recoveryToken = recoveryToken;
    }

    @Override
    public void setAddToken(byte[] addToken) {
        this.addToken = addToken;
    }

    @Override
    public void setRegistrationAddStart(LocalDateTime start) {
        this.registrationAddStart = start;
    }

    @Override
    public byte[] getAddToken() {
        return addToken;
    }

    @Override
    public LocalDateTime getRegistrationAddStart() {
        return registrationAddStart;
    }
}
