package com.mih.webauthn.demo;

import com.mih.webauthn.demo.sse.SseTemplate;
import io.github.webauthn.domain.WebAuthnUser;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class UserSseService {

    private static final AtomicInteger LOGIN = new AtomicInteger(0);
    private static final AtomicInteger REGISTER = new AtomicInteger(0);
    private static final String TOPIC = "USERS";

    private final Map<String, AtomicInteger> stats;
    private final SseTemplate template;

    public UserSseService(SseTemplate sseTemplate) {
        this.template = sseTemplate;
        stats = Map.of("login", LOGIN,
                "register", REGISTER);
    }

    public SseEmitter newSseEmitter() {
        return template.newSseEmitter(TOPIC, SseEmitter.event()
                .name("welcome")
                .data(stats)
                .id("1"));
    }

    public void broadcastLoggedInUser(WebAuthnUser user) {
        LOGIN.incrementAndGet();
        template.broadcast(TOPIC, SseEmitter.event()
                                            .data(stats)
                                            .id(user.getUsername())
                                            .name("login"));
    }


    public void broadcastNewUser(WebAuthnUser user) {

        REGISTER.incrementAndGet();
        template.broadcast(TOPIC, SseEmitter.event()
                                            .data(stats)
                                            .id(user.getUsername())
                                            .name("register"));
    }

    public void decrementLogin(WebAuthnUser user) {

        LOGIN.decrementAndGet();
        template.broadcast(TOPIC, SseEmitter.event()
                                            .data(stats)
                                            .id(user.getUsername())
                                            .name("logout"));
    }
}
