package com.mih.webauthn.demo;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.stream.Stream;

public class UIResourcesTest {

    @Test
    public void test() throws IOException {
        Stream.of(new ClassPathResource("static/")
                .getFile()
                .list())
                .forEach(s -> System.out.println("\"/" + s + "\","));
        Stream.of(new ClassPathResource("static/node_modules")
                .getFile()
                .list())
                .forEach(s -> System.out.println("\"/node_modules/" + s + "\","));
        Stream.of(new ClassPathResource("static/node_modules/web-authn-components")
                .getFile()
                .list())
                .forEach(s -> System.out.println("\"/node_modules/web-authn-components" + s + "\","));
    }
}
