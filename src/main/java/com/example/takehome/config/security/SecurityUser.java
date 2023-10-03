package com.example.takehome.config.security;

import com.example.takehome.model.TakehomeUser;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

@Getter
@EqualsAndHashCode(callSuper = true)
public class SecurityUser extends User {
    private final Long id;

    public SecurityUser(Long id,
                        String username,
                        String password,
                        Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
        this.id = id;
    }

    public SecurityUser(TakehomeUser user) {
        super(user.getUsername(), StringUtils.EMPTY,
              user.getAuthorities().stream().map(auth -> new SimpleGrantedAuthority(auth.getName())).toList());
        this.id = user.getId();
    }
}
