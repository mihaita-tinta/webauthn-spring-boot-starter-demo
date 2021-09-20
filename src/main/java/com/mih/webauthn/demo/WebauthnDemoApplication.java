package com.mih.webauthn.demo;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import com.mih.webauthn.demo.logs.StreamAppender;
import io.github.webauthn.WebAuthnFilter;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class WebauthnDemoApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(WebauthnDemoApplication.class, args);
//        context.start();
        addCustomAppender(context, (LoggerContext) LoggerFactory.getILoggerFactory());

    }

    private static void addCustomAppender(ConfigurableApplicationContext context, LoggerContext loggerContext) {
        StreamAppender customAppender = context.getBean(StreamAppender.class);
        Logger demo = loggerContext.getLogger(WebAuthnFilter.class.getPackageName());
        demo.setLevel(Level.DEBUG);
        demo.addAppender(customAppender);
        Logger mih = loggerContext.getLogger(WebauthnDemoApplication.class.getPackageName());
        mih.setLevel(Level.DEBUG);
        mih.addAppender(customAppender);
    }

}
