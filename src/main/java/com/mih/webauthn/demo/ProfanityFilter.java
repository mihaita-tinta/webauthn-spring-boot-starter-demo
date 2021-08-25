package com.mih.webauthn.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

@Service
public class ProfanityFilter {

    private static final Logger log = LoggerFactory.getLogger(ProfanityFilter.class);
    private Set<String> words = new ConcurrentSkipListSet<>();

    public ProfanityFilter() {
        try {
            loadConfigs();
        } catch (IOException e) {
            log.warn("warning - application runs without filtering due to config error: ", e);
        }
    }

    public boolean isOk(String input) {
        return !words.contains(input.toLowerCase().replace(" ", ""));
    }

    public void loadConfigs() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(new URL("https://docs.google.com/spreadsheets/d/1hIEi2YG3ydav1E06Bzf2mQbGZ12kh2fe4ISgLg_UBuM/export?format=csv").openConnection().getInputStream()));
        String line = "";
        int counter = 0;
        while ((line = reader.readLine()) != null) {
            counter++;
            String[] content = null;
            content = line.split(",");
            if (content.length == 0) {
                continue;
            }
            String word = content[0];
            words.add(word.toLowerCase().replace(" ", ""));

        }
        log.info("Loaded " + counter + " words to filter out");

    }

}
