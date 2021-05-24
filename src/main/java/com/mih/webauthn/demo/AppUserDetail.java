package com.mih.webauthn.demo;

import com.mih.webauthn.domain.WebAuthnUser;
import org.springframework.security.core.GrantedAuthority;

import java.util.Set;

public class AppUserDetail {

    private final Long appUserId;

    private final String username;

    private final Set<GrantedAuthority> authorities;

    public AppUserDetail(WebAuthnUser user, GrantedAuthority authority) {
        this.appUserId = user.getId();
        this.username = user.getUsername();
        this.authorities = Set.of(authority);
    }

    public Long getAppUserId() {
        return this.appUserId;
    }

    public String getUsername() {
        return this.username;
    }

    public Set<GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

}
