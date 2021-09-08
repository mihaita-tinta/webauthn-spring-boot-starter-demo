package com.mih.webauthn.demo;

import com.mih.webauthn.demo.logs.StreamAppender;
import com.mih.webauthn.demo.sse.SseTemplate;
import io.github.webauthn.domain.WebAuthnUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@RestController
//@CrossOrigin(value = "http://localhost:3000", allowCredentials = "true")
public class SseController {
    private Map<String, Integer> allWords = new ConcurrentHashMap<>();
    private Map<WebAuthnUser, Integer> userWordCounter = new ConcurrentHashMap<>();

    @Autowired
    SseTemplate template;

    @Autowired
    UserSseService sseService;

    @Autowired
    ProfanityFilter profanityFilter;

    @GetMapping(path = "/users", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter users() {
        return sseService.newSseEmitter();
    }

    @GetMapping(path = "/logs", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter logs() {
        return template.newSseEmitter(StreamAppender.TOPIC);
    }

    @GetMapping(path = "/feedback", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter feedback() {
        return template.newSseEmitter("feedback",
                SseEmitter.event()
                .data(allWords)
                .name("feedback"));
    }

    @PutMapping(path = "/feedback")
    public void feedback(@RequestParam String words, @AuthenticationPrincipal WebAuthnUser user) {

        int availableWords = userWordCounter.getOrDefault(user, 5);
        if ( availableWords > 0) {
            if (profanityFilter.isOk(words)) {
                    allWords.putIfAbsent(words, 0);
                    allWords.put(words, allWords.compute(words, (key, value) -> ++value));
                template.broadcast("feedback", SseEmitter.event()
                        .data(allWords)
                        .name("feedback"));
            }
            availableWords--;
            userWordCounter.put(user, availableWords);
        }
    }

}
