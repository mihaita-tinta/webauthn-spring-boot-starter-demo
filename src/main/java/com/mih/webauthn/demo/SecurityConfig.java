package com.mih.webauthn.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mih.webauthn.EnableWebAuthn;
import com.mih.webauthn.config.WebauthnConfigurer;
import com.yubico.webauthn.RelyingParty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.session.HttpSessionEventPublisher;

import javax.servlet.http.HttpSession;

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
                .antMatchers(
                        "/",
                        "/users", // TODO remove me
                        "/users/publish", // TODO remove me
                        "/actuator/**",

                        "/client.js",
                        "/ws.html",
                        "/error", "/h2-console/**",
                        "/svg",
                        "/36.b8e9f171696427e4761e.js",
                        "/38.c457b66303ddf6f33115.js",
                        "/42.1091edd820fd310ca794.js",
                        "/29.13bea3f9efc597116f5e.js",
                        "/56.9ca68d48dec63030a308.js",
                        "/10.3a19706d0e1d592abe52.js",
                        "/13.b404dd0ea3634d6d02d9.js",
                        "/index.html",
                        "/14.5f46ca2dee09cf98f4f6.js",
                        "/52.998bad56cf44a519018e.js",
                        "/34.408d51aa5a5cb3be1c26.js",
                        "/polyfills-dom.bf6c5ee357b1ca4890e9.js",
                        "/57.c8845c24df5d0bbd6a22.js",
                        "/12.ea925409c9520201e4f8.js",
                        "/3rdpartylicenses.txt",
                        "/32.5020e6fef2e3c8a8d807.js",
                        "/54.64df678d20e1fec6b6e9.js",
                        "/20.3d1756cd6bd0589b3919.js",
                        "/27.1de5f0678df0b28b0595.js",
                        "/45.4c2e2b33ea99fd5c9cbf.js",
                        "/24.05375985a71b1cad323e.js",
                        "/runtime.5b7309a458213e166be8.js",
                        "/styles.4d01d30f558b665d5066.css",
                        "/35.5546558a675ee979599a.js",
                        "/11.619689816ed3112ce996.js",
                        "/60.14896b60b302978b2e4e.js",
                        "/15.612f110055d0879647f1.js",
                        "/22.55b93da3dae4ee5bbc4a.js",
                        "/9.692769ff625bcf0361a1.js",
                        "/37.3aa8cd237b3b88cb8b7e.js",
                        "/44.a449861ce211ff7a3c5c.js",
                        "/58.8f99f87e08c163e4c730.js",
                        "/main.827668b303b1a72014f0.js",
                        "/17.0d246bf710e5d29d3fd7.js",
                        "/28.65b8d5a08e78c21f1801.js",
                        "/25.2465434d7970c3f7006d.js",
                        "/47.f93187ab797bdf716dae.js",
                        "/41.f2d207566257f3c73fa6.js",
                        "/39.32d7e1e1be4160e9b133.js",
                        "/18.3771e0e68aea1e112758.js",
                        "/31.d0aae1b6e066aec186ac.js",
                        "/40.2b87ae322f651360437d.js",
                        "/50.d9cd908c92dcda0469d9.js",
                        "/43.ffba9cff2c347ecd4300.js",
                        "/19.0161c3c3744d64badf26.js",
                        "/48.2ea5cb473aeac16eeb8c.js",
                        "/59.5a036f46b6d71f0c7fc8.js",
                        "/51.7cbdb78c03cedc0585a4.js",
                        "/16.61e3c887c649da3c63f6.js",
                        "/2.44af3d81c32de51599a6.js",
                        "/33.70b0d6bdc4886ca6fd48.js",
                        "/23.0e418c3632b71482049b.js",
                        "/polyfills-core-js.926b2b327e549d9db414.js",
                        "/30.b6f84f7e9d39a91240f4.js",
                        "/polyfills.366a5623aadf5a7905ce.js",
                        "/46.ddc8d1562f6e3d236930.js",
                        "/common.c224a8c5e71f04fe1086.js",
                        "/26.7ab327fb9764fee88734.js",
                        "/assets",
                        "/21.1fccccad93f6054d4acc.js",
                        "/53.fa72872455b0b34d4a3b.js",
                        "/polyfills-css-shim.23bd7f8217c53f670e61.js",
                        "/55.3b692fe1511b8da674a3.js",
                        "/49.b664d1deb4a9ae98f7f8.js",
                        "/assets/favicon.png",
                        "/assets/logo.png",
                        "/svg/chevron-back.svg",
                        "/svg/arrow-back-sharp.svg"
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
                .apply(new WebauthnConfigurer()
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
