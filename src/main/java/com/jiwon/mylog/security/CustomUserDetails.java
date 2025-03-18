package com.jiwon.mylog.security;

import com.jiwon.mylog.entity.user.User;
import java.util.Collection;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@AllArgsConstructor
public class CustomUserDetails implements UserDetails {

    private final User user;
    private final List<SimpleGrantedAuthority> authorities;

    public CustomUserDetails(User user) {
        this.user = user;
        this.authorities = user.getUserRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .toList();
    }

    public Long getUserId() {
        return user.getId();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }
}
