package com.mih.webauthn.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mih.webauthn.EnableWebAuthn;
import com.mih.webauthn.config.WebauthnConfigurer;
import com.mih.webauthn.repository.AppCredentialsRepository;
import com.mih.webauthn.repository.AppUserRepository;
import com.mih.webauthn.service.CredentialService;
import com.yubico.webauthn.RelyingParty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;

@Configuration
@EnableWebSecurity
@EnableWebAuthn
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    AppUserRepository appUserRepository;
    @Autowired
    AppCredentialsRepository credentialRepository;
    @Autowired
    CredentialService credentialService;
    @Autowired
    RelyingParty relyingParty;
    @Autowired
    ObjectMapper mapper;

    @Override
    public void configure(WebSecurity web) {
        web
                .ignoring()
                .antMatchers("/error");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf(customizer -> customizer.disable())
                .logout(customizer -> {
                    customizer.logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler());
                    customizer.deleteCookies("JSESSIONID");
                })
                .authorizeRequests()
                .anyRequest()
                .authenticated()
                .and()
                .exceptionHandling(customizer -> customizer
                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
                .apply(
                        new WebauthnConfigurer(appUserRepository, credentialRepository,
                                                credentialService, relyingParty, mapper)
                .successHandler(u -> {

                    AppUserDetail userDetail = new AppUserDetail(u,
                            new SimpleGrantedAuthority("USER"));
                    AppUserAuthentication auth = new AppUserAuthentication(userDetail);
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }));
    }

    @Bean
    @Override
    public UserDetailsService userDetailsService() {
        return new InMemoryUserDetailsManager();
    }
}
