package com.mih.webauthn.demo;

import com.mih.webauthn.demo.domain.Account;
import com.mih.webauthn.demo.domain.AccountRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class HomeController {

    @Autowired
    AccountRepo accountRepo;
    @Autowired
    UserDetailsService userDetailsService;

    @GetMapping("/")
    public Map<String, Object> accounts(@AuthenticationPrincipal UserDetails user) {
        Iterable<Account> accounts = accountRepo.findAllByUsername(user.getUsername());
        return Map.of("user", user,
                "accounts", accounts);
    }
}
