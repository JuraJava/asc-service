package com.yurdan.ascService.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class JwtAuthentication extends AbstractAuthenticationToken {

    private UUID uuid;

    public JwtAuthentication(UUID uuid, List<SimpleGrantedAuthority> authorities) {
        super(authorities);
        this.uuid = uuid;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return uuid;
    }
}
