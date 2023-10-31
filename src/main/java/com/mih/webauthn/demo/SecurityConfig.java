package com.mih.webauthn.demo;

import com.mih.webauthn.demo.domain.Account;
import com.mih.webauthn.demo.domain.AccountRepo;
import io.github.webauthn.EnableWebAuthn;
import io.github.webauthn.config.WebAuthnConfigurer;
import io.github.webauthn.config.WebAuthnUsernameAuthenticationToken;
import io.github.webauthn.domain.DefaultWebAuthnUser;
import io.github.webauthn.domain.WebAuthnUser;
import io.github.webauthn.domain.WebAuthnUserRepository;
import io.github.webauthn.events.NewRecoveryTokenCreated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Collections;
import java.util.Map;


@Configuration
@EnableJpaRepositories
@EntityScan("com.mih.webauthn.demo")
@EnableWebAuthn
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    MyUserDetailsService userDetailsService;
    @Autowired
    AccountRepo accountRepo;

    @Autowired
    WebAuthnUserRepository<WebAuthnUser> webAuthnUserRepository;

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
                // Spring Security should completely ignore URLs starting with /resources/
                .requestMatchers("/index.html", "/login.html",
                        "/h2-console/**",
                        "/register.html", "/recovery.html", "/node_modules/**", "/error");
    }

    @EventListener
    void newUser(NewRecoveryTokenCreated tokenCreated) {
        Account account = new Account();
        account.setUsername(tokenCreated.user().getUsername());
        account.setName("account-" + System.currentTimeMillis());
        accountRepo.save(account);
    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(c -> c.disable())
                .authorizeRequests()
                .anyRequest()
                .authenticated()
                .and()
//                .formLogin(Customizer.withDefaults())
                .logout(customizer -> {
                    customizer.logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler());
                    customizer.deleteCookies("JSESSIONID");
                })
                .apply(new WebAuthnConfigurer()
                                .defaultLoginSuccessHandler((user, credentials) -> {
                                    System.out.println("loggedin");
                                    UsernamePasswordAuthenticationToken token = new WebAuthnUsernameAuthenticationToken(user, credentials, Collections.emptyList());
                                    SecurityContextHolder.getContext().setAuthentication(token);
                                    ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
                                    attr.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext(), RequestAttributes.SCOPE_SESSION);
                                })
//                                .userSupplier(() -> {
//                                    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//                                    if (authentication == null) {
//                                        return null;
//                                    }
//                                    return webAuthnUserRepository.findByUsername(authentication.getName())
//                                            .orElseGet(() -> {
//                                                DefaultWebAuthnUser newUser = new DefaultWebAuthnUser();
//                                                newUser.setUsername(authentication.getName());
//                                                newUser.setEnabled(true);
//                                                return webAuthnUserRepository.save(newUser);
//                                            });
//                                })

                )
        ;

        return http.build();
    }

//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
//    }

//    @Bean
//    public DaoAuthenticationProvider authenticationProvider() {
//        DaoAuthenticationProvider auth = new DaoAuthenticationProvider();
//        auth.setUserDetailsService(userDetailsService());
//        auth.setPasswordEncoder(passwordEncoder());
//        return auth;
//    }

//    @Bean
//    public UserDetailsService userDetailsService() {
//        UserDetails user = User.withDefaultPasswordEncoder()
//                .username("user")
//                .password("a")
//                .roles("USER")
//                .build();
//        UserDetails admin = User.withDefaultPasswordEncoder()
//                .username("admin")
//                .password("a")
//                .roles("ADMIN", "USER")
//                .build();
//        return new InMemoryUserDetailsManager(user, admin);
//    }

}
