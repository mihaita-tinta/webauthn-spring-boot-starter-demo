package com.mih.webauthn.demo;

import com.mih.webauthn.domain.WebAuthnCredentials;
import com.mih.webauthn.domain.WebAuthnCredentialsRepository;
import com.mih.webauthn.domain.WebAuthnUser;
import com.mih.webauthn.domain.WebAuthnUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class PrivateResource {
    private static final Logger log = LoggerFactory.getLogger(PrivateResource.class);

    @Autowired
    WebAuthnCredentialsRepository credentialsRepository;
    @Autowired
    WebAuthnUserRepository userRepository;

    @GetMapping("/me")
    public Map<String, Object> me(@AuthenticationPrincipal WebAuthnUser user) {
        log.info("me - user: {}", user);
        return Map.of("username", user.getUsername(),
                "id", user.getId().toString(),
                "credentials", credentialsRepository.findAllByAppUserId(user.getId()));
    }

    @GetMapping("/devices")
    public List<Map<String, Object>> devices(Authentication token) {
        WebAuthnUser user = (WebAuthnUser) token.getPrincipal();
        WebAuthnCredentials currentCredentials = (WebAuthnCredentials) token.getCredentials();
        List<Map<String, Object>> map = credentialsRepository
                .findAllByAppUserId(user.getId())
                .stream()
                .map(credentials ->
                        Map.<String, Object>of("id", credentials.getId(),
                                "userAgent", credentials.getUserAgent() == null ? "N/A" : credentials.getUserAgent(),
                                "currentDevice", currentCredentials.getId().equals(credentials.getId())))
                .collect(Collectors.toList());
        log.debug("devices - user: {}, devices: {}", user, map);
        return map;
    }

    @DeleteMapping("/devices/{deviceId}")
    public void deleteDevice(@AuthenticationPrincipal WebAuthnUser user, @PathVariable Long deviceId) {

        credentialsRepository
                .findAllByAppUserId(user.getId())
                .stream()
                .filter(c -> c.getId().equals(deviceId))
                .findAny()
                .ifPresent(c -> credentialsRepository.deleteById(deviceId));

        if (credentialsRepository.findAllByAppUserId(user.getId())
                .isEmpty()) {
            log.info("deleteDevice - user {} has no longer any device. Deleting user too . . .", user);
            userRepository.deleteById(user.getId());
        }
    }
}
