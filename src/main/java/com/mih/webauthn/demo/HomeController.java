package com.mih.webauthn.demo;

import com.mih.webauthn.demo.domain.Account;
import com.mih.webauthn.demo.domain.AccountRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class HomeController {

    @Autowired
    AccountRepo accountRepo;
    @Autowired
    UserDetailsService userDetailsService;

    @GetMapping("/")
    public SomeDetailsForCurrentUser accounts(@AuthenticationPrincipal UserDetails user) {
        var accounts = accountRepo.findAllByUsername(user.getUsername());
        return new SomeDetailsForCurrentUser(user, accounts);
    }

    public record SomeDetailsForCurrentUser(UserDetails user, List<Account> accounts){}
}
