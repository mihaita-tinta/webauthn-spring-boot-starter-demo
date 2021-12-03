package com.mih.webauthn.demo.socket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mih.webauthn.demo.RandomCodeService;
import io.github.webauthn.WebAuthnProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

@Configuration
@EnableWebSocket
public class WebSocketConfiguration implements WebSocketConfigurer {

    @Autowired
    WebAuthnProperties properties;
    @Autowired
    ObjectMapper mapper;
    @Autowired
    RandomCodeService codeService;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new SocketHandler(mapper, codeService), "/socket")
                .addInterceptors(new HttpSessionHandshakeInterceptor())
                .setAllowedOrigins(properties.getRelyingPartyOrigins().toArray(new String[]{}));
    }
}
