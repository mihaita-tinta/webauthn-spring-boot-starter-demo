package com.mih.webauthn.demo;


import io.github.webauthn.domain.WebAuthnCredentialsRepository;
import io.github.webauthn.domain.WebAuthnUser;
import io.github.webauthn.domain.WebAuthnUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class PrivateResource {
    private static final Logger log = LoggerFactory.getLogger(PrivateResource.class);

    @Autowired
    WebAuthnCredentialsRepository credentialsRepository;
    @Autowired
    WebAuthnUserRepository userRepository;
    @Autowired
    AccountRepository accountRepository;

    @GetMapping("/me")
    public Map<String, Object> me(@AuthenticationPrincipal WebAuthnUser user) {
        log.info("me - user: {}", user);
        return Map.of("username", user.getUsername(),
                "id", user.getId().toString(),
                "credentials", credentialsRepository.findAllByAppUserId(user.getId()));
    }

    @GetMapping("/accounts")
    public Map<String, Object> accounts(@AuthenticationPrincipal WebAuthnUser user) {
        log.info("accounts - user: {}", user);

        return Map.of("accounts", accountRepository.findAll());
    }

}
