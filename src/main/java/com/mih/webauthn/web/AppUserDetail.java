package com.mih.webauthn.web;

import com.mih.webauthn.domain.AppUser;
import org.springframework.security.core.GrantedAuthority;

import java.util.Set;

public class AppUserDetail {

    private final Long appUserId;

    private final String username;

    private final Set<GrantedAuthority> authorities;

    public AppUserDetail(AppUser user, GrantedAuthority authority) {
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
