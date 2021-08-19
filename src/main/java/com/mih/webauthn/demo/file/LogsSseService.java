package com.mih.webauthn.demo.file;

import com.mih.webauthn.demo.sse.SseTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.nio.file.Files;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class LogsSseService {
    private static final Logger log = LoggerFactory.getLogger(LogsSseService.class);

    private static final String TOPIC = "logs";
    private final SseTemplate template;
    private static final AtomicLong COUNTER = new AtomicLong(0);

    public LogsSseService(SseTemplate template, MonitoringFileService monitoringFileService) {
        this.template = template;
        monitoringFileService.listen(file -> {

            try {
                long startIndex = COUNTER.get();
                Files.lines(file)
                        .skip(startIndex)
                        .forEach(line ->
                                template.broadcast(TOPIC, SseEmitter.event()
                                        .id(String.valueOf(COUNTER.incrementAndGet()))
                                        .data(line)));


            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public SseEmitter newSseEmitter() {
        return template.newSseEmitter(TOPIC);
    }
}
