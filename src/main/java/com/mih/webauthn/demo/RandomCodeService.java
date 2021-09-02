package com.mih.webauthn.demo;

import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Locale;
import java.util.Random;

@Service
public class RandomCodeService {
    private static final String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    private final Random random;

    private final char[] symbols;


    public RandomCodeService() {
        this.random = new SecureRandom();
        this.symbols = upper.toCharArray();
    }

    public String nextString() {
        char[] buf = new char[6];
        for (int idx = 0; idx < buf.length; ++idx)
            buf[idx] = symbols[random.nextInt(symbols.length)];
        return new String(buf);
    }
    public String nextString(int length) {
        char[] buf = new char[length];
        for (int idx = 0; idx < buf.length; ++idx)
            buf[idx] = symbols[random.nextInt(symbols.length)];
        return new String(buf);
    }

}
