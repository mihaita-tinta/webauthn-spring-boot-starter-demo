package com.mih.webauthn.demo;

import com.mih.webauthn.domain.WebAuthnUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class PrivateResource {
    private static final Logger log = LoggerFactory.getLogger(PrivateResource.class);

    @GetMapping("/me")
    public Map<String, String> secretMessage(@AuthenticationPrincipal WebAuthnUser user) {
        log.info("user id:  " + user.getId());
        log.info("username: " + user.getUsername());
        return Map.of("username", user.getUsername());
    }
}
