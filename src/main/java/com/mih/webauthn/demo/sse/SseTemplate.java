package com.mih.webauthn.demo.sse;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class SseTemplate {

    private final Map<String, List<SseEmitter>> connections = new ConcurrentHashMap<>();

    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    public SseEmitter newSseEmitter(String topic) {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        connections.putIfAbsent(topic, new CopyOnWriteArrayList<>());
        connections.get(topic)
                .add(emitter);
        executorService.submit(() -> {
            sendMessage(SseEmitter.event()
                    .name("welcome")
                    .data("welcome")
                    .id("1"), emitter);
        });
        return emitter;
    }
    public SseEmitter newSseEmitter(String topic, SseEmitter.SseEventBuilder... events) {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        connections.putIfAbsent(topic, new CopyOnWriteArrayList<>());
        connections.get(topic)
                .add(emitter);
        executorService.submit(() -> {
            if (events != null) {
                Arrays.asList(events)
                        .forEach(event -> sendMessage(event, emitter));
            }
        });
        return emitter;
    }


    @Async
    public void broadcast(String topic, SseEmitter.SseEventBuilder event) {

        Optional.ofNullable(connections.get(topic))
                .ifPresent(sseEmitters -> {
                    List<SseEmitter> unavailableEmitters = sseEmitters
                            .stream()
                            .flatMap(sseEmitter -> Stream.of(sendMessage(event, sseEmitter))
                                    .filter(isSuccess -> !isSuccess)
                                    .map(a -> sseEmitter))
                            .collect(Collectors.toList());
                    sseEmitters.removeAll(unavailableEmitters);
                });
    }

    /**
     * @param event
     * @param sseEmitter
     * @return message sent successfully
     */
    public boolean sendMessage(SseEmitter.SseEventBuilder event, SseEmitter sseEmitter) {
        try {
            sseEmitter.send(event);
            return true;
        } catch (Exception ex) {
            sseEmitter.completeWithError(ex);
            return false;
        }
    }
}
