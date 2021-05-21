package com.mih.webauthn.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mih.webauthn.repository.AppCredentialsRepository;
import com.mih.webauthn.repository.AppUserRepository;
import com.mih.webauthn.service.CredentialRepositoryService;
import com.yubico.webauthn.RelyingParty;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;

public class WebauthnConfigurer extends AbstractHttpConfigurer<WebauthnConfigurer, HttpSecurity> {

    private final AppUserRepository appUserRepository;
    private final AppCredentialsRepository credentialRepository;
    private final CredentialRepositoryService credentialService;
    private final RelyingParty relyingParty;
    private final ObjectMapper mapper;

    public WebauthnConfigurer(AppUserRepository appUserRepository, AppCredentialsRepository credentialRepository, CredentialRepositoryService credentialService, RelyingParty relyingParty, ObjectMapper mapper) {
        this.appUserRepository = appUserRepository;
        this.credentialRepository = credentialRepository;
        this.credentialService = credentialService;
        this.relyingParty = relyingParty;
        this.mapper = mapper;
    }

    @Override
    public void init(HttpSecurity http) {
        // initialization code
    }

    @Override
    public void configure(HttpSecurity http) {
        WebAuthnFilter filter = new WebAuthnFilter(appUserRepository, credentialRepository, credentialService, relyingParty, mapper);
        http.addFilterBefore(filter, FilterSecurityInterceptor.class);
    }
}
