package com.mih.webauthn.demo;

import com.mih.webauthn.domain.WebAuthnUser;
import org.springframework.security.core.GrantedAuthority;

import java.util.Set;

public class AppUserDetail {

    private final WebAuthnUser user;

    private final Set<GrantedAuthority> authorities;

    public AppUserDetail(WebAuthnUser user, GrantedAuthority authority) {
        this.user = user;
        this.authorities = Set.of(authority);
    }
    public WebAuthnUser getWebAuthnUser() {
        return user;
    };

    public Long getAppUserId() {
        return this.user.getId();
    }

    public String getUsername() {
        return this.user.getUsername();
    }

    public Set<GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

}
