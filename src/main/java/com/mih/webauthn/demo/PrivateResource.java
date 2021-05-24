package com.mih.webauthn.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PrivateResource {
    private static final Logger log = LoggerFactory.getLogger(PrivateResource.class);

    @GetMapping("/secret")
    public String secretMessage(@AuthenticationPrincipal AppUserDetail user) {
        log.info("user id:  " + user.getAppUserId());
        log.info("username: " + user.getUsername());
        return "a secret message";
    }
}
