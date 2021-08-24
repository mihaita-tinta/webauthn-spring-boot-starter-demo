package com.mih.webauthn.demo.logs;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import com.mih.webauthn.demo.sse.SseTemplate;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Component
public class StreamAppender extends UnsynchronizedAppenderBase<ILoggingEvent> implements SmartLifecycle {
    public static final String TOPIC = "logs";

    private final SseTemplate template;

    public StreamAppender(SseTemplate template) {
        this.template = template;
    }

    @Override
    protected void append(ILoggingEvent event) {
        template.broadcast(TOPIC, SseEmitter.event()
                .id(event.getThreadName())
                .data(event.getFormattedMessage()));
    }

    @Override
    public boolean isRunning() {
        return isStarted();
    }
}
