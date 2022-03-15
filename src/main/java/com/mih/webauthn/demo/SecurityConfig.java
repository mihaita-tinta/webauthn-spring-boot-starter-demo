package com.mih.webauthn.demo;

import com.mih.webauthn.demo.domain.Account;
import com.mih.webauthn.demo.domain.AccountRepo;
import io.github.webauthn.EnableWebAuthn;
import io.github.webauthn.config.WebAuthnConfigurer;
import io.github.webauthn.domain.DefaultWebAuthnUser;
import io.github.webauthn.domain.WebAuthnUser;
import io.github.webauthn.domain.WebAuthnUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;


@Configuration
@EnableJpaRepositories
@EntityScan("com.mih.webauthn.demo")
@EnableWebAuthn
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    MyUserDetailsService userDetailsService;
    @Autowired
    AccountRepo accountRepo;
    @Autowired
    WebAuthnUserRepository<WebAuthnUser> webAuthnUserRepository;

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/index.html", "/login.html",
                "/h2-console/**",
                "/register.html", "/recovery.html", "/node_modules/**", "/error");
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .headers().frameOptions().sameOrigin().and()
                .authorizeRequests()
                .anyRequest()
                .authenticated()
                .and()
                .formLogin()
                .and()
                .apply(new WebAuthnConfigurer()
                        .registerSuccessHandler(user -> {
                            Account account = new Account();
                            account.setUsername(user.getUsername());
                            account.setName("account-" + System.currentTimeMillis());
                            accountRepo.save(account);
                        })
                        .userSupplier(() -> {
                            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                            if (authentication == null) {
                                return null;
                            }
                            return webAuthnUserRepository.findByUsername(authentication.getName())
                                    .orElseGet(() -> {
                                        DefaultWebAuthnUser newUser = new DefaultWebAuthnUser();
                                        newUser.setUsername(authentication.getName());
                                        newUser.setEnabled(true);
                                        return webAuthnUserRepository.save(newUser);
                                    });
                        })
                );
    }
}
