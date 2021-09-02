package com.mih.webauthn.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mih.webauthn.EnableWebAuthn;
import com.mih.webauthn.config.WebAuthnConfigurer;
import com.yubico.webauthn.RelyingParty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.session.HttpSessionEventPublisher;

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
    UserSseService sseService;

    @Override
    public void configure(WebSecurity web) {
        web
                .ignoring()
                .antMatchers(HttpMethod.GET, "/feedback")
                .antMatchers(
                        "/",
                        "/registration", // TODO remove me
                        "/users", // TODO remove me
                        "/logs", // TODO remove me
                        "/users/publish", // TODO remove me
                        "/actuator/**",

                        "/index.html",
                        "/login.html",
                        "/new-device.html",
                        "/node_modules/web-authn-components/dist/**",
                        "/error",

                        "/images",
                        "/.well-known",
                        "/icons",
                        "/manifest.webmanifest",
                        "/assets",
                        "/sw.js",
                        "/workbox-7ce2238d.js",
                        "/assets/web-authn-add-new.db8c35b6.js",
                        "/assets/web-authn-rtc-enrollment-requester.6cfc5687.js",
                        "/assets/notification.cd1ebc8f.js",
                        "/assets/loaders.88df2441.js",
                        "/assets/Stats.461f4b25.js",
                        "/assets/authentication-challenges.c1386970.js",
                        "/assets/Register.fb2692f1.js",
                        "/assets/Login.fadb0fbe.js",
                        "/assets/web-authn-recovery.7cca9a32.js",
                        "/assets/authentication.7a9077c1.js",
                        "/assets/Dashboard.7aee49d0.js",
                        "/assets/authentication-how.6e219fb4.js",
                        "/assets/vendor.3b2ac730.js",
                        "/assets/rtc.e73f50c1.js",
                        "/assets/index.8900d57a.css",
                        "/assets/parse.30983023.js",
                        "/assets/web-authn-add-new.17a2b6aa.css",
                        "/assets/web-authn-recover.e2e8fbe3.js",
                        "/assets/slides.9ba8ae6b.js",
                        "/assets/cards.4765d79c.js",
                        "/assets/forms.ed77d288.js",
                        "/assets/cover.dcc3cb96.js",
                        "/assets/notifications.534c00d2.js",
                        "/assets/web-authn-support.53eba617.js",
                        "/assets/index.1014ec76.js",
                        "/assets/notifications.9a330297.css",
                        "/assets/Home.8aef007d.js",
                        "/icons/android-chrome-192x192.png",
                        "/icons/apple-touch-icon.png",
                        "/icons/android-chrome-512x512.png",
                        "/images/authentication-how.png",
                        "/images/authentication.png",
                        "/images/web-authn-support.png",
                        "/images/authentication-challenges.png",
                        "/images/ing-logo-bright.svg",
                        "/images/fido2-support.png",
                        "/images/ing-logo-dark.svg"
                );
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf(customizer -> customizer.disable())
                .logout(customizer -> {
                    customizer.logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler());
                    customizer.deleteCookies("JSESSIONID");
                })
                .authorizeRequests()
                .antMatchers("/socket")
                .access("isAuthenticated() or isAnonymous()")
                .and()
                .authorizeRequests()
                .anyRequest()
                .authenticated()
                .and()
                .apply(new WebAuthnConfigurer()
                        .defaultLoginSuccessHandler((user, credentials) -> sseService.broadcastLoggedInUser(user))
                        .registerSuccessHandler(user -> sseService.broadcastNewUser(user))
                );
    }

    @Bean
    public HttpSessionEventPublisher sessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }

    @Bean
    @Override
    public UserDetailsService userDetailsService() {
        return new InMemoryUserDetailsManager();
    }
}
