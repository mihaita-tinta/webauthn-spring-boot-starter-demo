package com.mih.webauthn.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yubico.webauthn.RelyingParty;
import io.github.webauthn.EnableWebAuthn;
import io.github.webauthn.config.WebAuthnConfigurer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;

@Configuration
@EnableWebSecurity
@EnableWebAuthn
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private static final Logger log = LoggerFactory.getLogger(SecurityConfig.class);

    @Autowired
    RelyingParty relyingParty;
    @Autowired
    ObjectMapper mapper;
    @Autowired
    AccountRepository accountRepository;

    @Override
    public void configure(WebSecurity web) {
        web
                .ignoring()
                .antMatchers(
                        "/",
                        "/index.html",
                        "/login.html",
                        "/new-device.html",
                        "/node_modules/web-authn-components/dist/**",
                        "/error"
                );
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .headers().frameOptions().sameOrigin()
                .and()
                .logout(customizer -> {
                    customizer.logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler());
                    customizer.deleteCookies("JSESSIONID");
                })
                .authorizeRequests()
                .anyRequest()
                .authenticated()
                .and()
                .apply(new WebAuthnConfigurer()
                        .defaultLoginSuccessHandler((user, credentials) -> log.info("user logged in: {}", user))
                        .registerSuccessHandler(user -> {
                            log.info("new user registered: {}", user);

                            Account account = new Account();
                            account.setIban(user.getUsername() + "-iban-" + System.currentTimeMillis());
                            accountRepository.save(account);
                        })
                );
    }
}
