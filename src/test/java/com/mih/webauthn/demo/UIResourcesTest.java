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
        Stream.of(new ClassPathResource("static/assets")
                .getFile()
                .list())
                .forEach(s -> System.out.println("\"/assets/" + s + "\","));
        Stream.of(new ClassPathResource("static/svg")
                .getFile()
                .list())
                .forEach(s -> System.out.println("\"/svg/" + s + "\","));
    }
}
