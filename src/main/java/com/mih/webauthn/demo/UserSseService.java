package com.mih.webauthn.demo;

import io.github.webauthn.domain.WebAuthnUser;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class UserSseService {

    private static final AtomicInteger LOGIN = new AtomicInteger(0);
    private static final AtomicInteger REGISTER = new AtomicInteger(0);
    private final List<SseEmitter> connections = new CopyOnWriteArrayList<>();
    private final ExecutorService sseMvcExecutor = Executors.newSingleThreadExecutor();

    private final Map<String, AtomicInteger> stats;

    public UserSseService() {
        stats = Map.of("login", LOGIN,
                "register", REGISTER);
    }

    public SseEmitter newSseEmitter() {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        sseMvcExecutor.execute(() -> {
            sendMessage("1", "welcome", emitter);
        });
        connections.add(emitter);
        return emitter;
    }

    public void broadcastLoggedInUser(WebAuthnUser user) {

        LOGIN.incrementAndGet();
        sendMessageToAllConnections(user, "login");
    }


    public void broadcastNewUser(WebAuthnUser user) {

        REGISTER.incrementAndGet();
        sendMessageToAllConnections(user, "register");
    }

    public void decrementLogin(WebAuthnUser user) {

        LOGIN.decrementAndGet();
        sendMessageToAllConnections(user, "logout");
    }

    private void sendMessageToAllConnections(WebAuthnUser user, String type) {
        List<SseEmitter> toBeDeleted = connections
                .stream()
                .map(sseEmitter -> sendMessage(user, type, sseEmitter))
                .filter(s -> s != null)
                .collect(Collectors.toList());
        connections.removeAll(toBeDeleted);
    }

    private SseEmitter sendMessage(WebAuthnUser user, String type, SseEmitter sseEmitter) {
        return sendMessage(user.getUsername(), type, sseEmitter);
    }

    private SseEmitter sendMessage(String id, String type, SseEmitter sseEmitter) {
        try {
            SseEmitter.SseEventBuilder event = SseEmitter.event()
                    .data(stats)
                    .id(id)
                    .name(type);
            sseEmitter.send(event);
            return null;
        } catch (Exception ex) {
            sseEmitter.completeWithError(ex);
            return sseEmitter;
        }
    }
}
