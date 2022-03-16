package com.mih.webauthn.demo;

import com.mih.webauthn.demo.domain.Account;
import com.mih.webauthn.demo.domain.AccountRepo;
import com.mih.webauthn.demo.domain.MyUser;
import com.mih.webauthn.demo.domain.MyUserRepo;
import io.github.webauthn.domain.DefaultWebAuthnUser;
import io.github.webauthn.domain.WebAuthnUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.stream.IntStream;

@Configuration
public class UsersInitializer {

    @Autowired
    AccountRepo accountRepo;
    @Autowired
    MyUserRepo userRepo;
    @Autowired
    PasswordEncoder encoder;
    @Autowired
    WebAuthnUserRepository webAuthnUserRepository;

    @Bean
    public CommandLineRunner initUsers() {
        return args -> {
            IntStream.range(0, 10)
                    .forEach(i -> {
                        MyUser user = new MyUser();
                        user.setUsername("user-" + i);
                        user.setPassword(encoder.encode("a"));
                        userRepo.save(user);
                        createAccount(user.getUsername());
                        createWebAuthn(user.getUsername());
                    });
        };
    }

    private void createWebAuthn(String username) {

        DefaultWebAuthnUser newUser = new DefaultWebAuthnUser();
        newUser.setUsername(username);
        newUser.setEnabled(true);
        webAuthnUserRepository.save(newUser);
    }

    private Account createAccount(String username) {
        Account account = new Account();
        account.setUsername(username);
        account.setName("account-name" + System.currentTimeMillis());
        accountRepo.save(account);
        return account;
    }
}
