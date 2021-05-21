package com.mih.webauthn.config;

import com.mih.webauthn.repository.AppCredentialsRepository;
import com.mih.webauthn.repository.AppUserRepository;
import com.mih.webauthn.web.AppUserDetail;
import com.yubico.webauthn.RelyingParty;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

public class WebAuthnRegistrationAddStrategy {

    private final AppUserRepository appUserRepository;
    private final AppCredentialsRepository credentialRepository;
    private final SecureRandom random = new SecureRandom();

    public WebAuthnRegistrationAddStrategy(AppUserRepository appUserRepository, AppCredentialsRepository credentialRepository) {
        this.appUserRepository = appUserRepository;
        this.credentialRepository = credentialRepository;
    }

    public byte[] registrationAdd(AppUserDetail user) {
        byte[] addToken = new byte[16];
        this.random.nextBytes(addToken);

        this.appUserRepository.findById(user.getAppUserId())
                .map(u -> {
                    u.setAddToken(addToken);
                    u.setRegistrationAddStart(LocalDateTime.now());
                    return appUserRepository.save(u);
                })
                .orElseThrow();

        return Base64.getEncoder().encode(addToken);
    }
}
