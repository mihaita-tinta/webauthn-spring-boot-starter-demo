package com.mih.webauthn.demo;

import com.mih.webauthn.demo.logs.StreamAppender;
import com.mih.webauthn.demo.sse.SseTemplate;
import com.mih.webauthn.domain.WebAuthnUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
public class SseController {

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
        return template.newSseEmitter("feedback");
    }

    @PutMapping(path = "/feedback")
    public void feedback(@RequestParam String words, @AuthenticationPrincipal WebAuthnUser user) {

        if (profanityFilter.isOk(words)) {
            template.broadcast("feedback", SseEmitter.event()
                    .data(words)
                    .id(user.getUsername()));
        }
    }

}
