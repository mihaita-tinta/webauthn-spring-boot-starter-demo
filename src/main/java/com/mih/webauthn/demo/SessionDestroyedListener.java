package com.mih.webauthn.demo;

import com.mih.webauthn.domain.WebAuthnUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.security.web.session.HttpSessionDestroyedEvent;
import org.springframework.stereotype.Component;

@Component
public class SessionDestroyedListener {

    @Autowired
    UserSseService sseService;

    @EventListener
    public void sessionDestroyed(HttpSessionDestroyedEvent sessionDestroyedEvent) {
        sessionDestroyedEvent.getSecurityContexts()
                .stream()
                .map(s -> (WebAuthnUser) s.getAuthentication().getPrincipal())
                .findFirst()
                .ifPresent(user -> sseService.decrementLogin(user));
    }
}
