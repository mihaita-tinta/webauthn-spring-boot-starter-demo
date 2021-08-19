package com.mih.webauthn.demo.file;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

class MonitoringFileServiceTest {
    private static final Logger log = LoggerFactory.getLogger(MonitoringFileServiceTest.class);


    @Test
    public void test() throws IOException, InterruptedException {

        CountDownLatch latch = new CountDownLatch(1);
        new MonitoringFileService("logs", "spring.log")
                .listen(file -> {
                    log.info("test - changed: " + file);
                    latch.countDown();
                });

        latch.await();
    }
}
