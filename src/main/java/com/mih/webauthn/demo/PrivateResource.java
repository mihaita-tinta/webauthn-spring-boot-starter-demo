package com.mih.webauthn.demo;

import com.mih.webauthn.domain.WebAuthnCredentialsRepository;
import com.mih.webauthn.domain.WebAuthnUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class PrivateResource {
    private static final Logger log = LoggerFactory.getLogger(PrivateResource.class);

    @Autowired
    WebAuthnCredentialsRepository credentialsRepository;

    @GetMapping("/me")
    public Map<String, String> secretMessage(@AuthenticationPrincipal WebAuthnUser user) {
        log.info("user id:  " + user.getId());
        log.info("username: " + user.getUsername());
        return Map.of("username", user.getUsername());
    }

    @GetMapping("/devices")
    public List<Map<String, String>> devices(@AuthenticationPrincipal WebAuthnUser user) {
        log.info("devices user id:  " + user.getId());
        return credentialsRepository
                .findAllByAppUserId(user.getId())
                .stream()
                .map(credentials ->
                        Map.of("id", Base64.getEncoder().encodeToString(credentials.getCredentialId()),
                                "userAgent", credentials.getUserAgent()))
                .collect(Collectors.toList());
    }

    @DeleteMapping("/devices/{deviceId}")
    public void deleteDevice(@PathVariable String deviceId) {
        credentialsRepository.deleteById(Base64.getDecoder().decode(deviceId));
    }
}
