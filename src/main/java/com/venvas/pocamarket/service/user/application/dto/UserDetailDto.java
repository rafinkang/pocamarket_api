package com.venvas.pocamarket.service.user.application.dto;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.venvas.pocamarket.service.user.domain.enums.UserGrade;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class UserDetailDto implements UserDetails {

    private final String uuid;
    private final UserGrade grade;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(this.getGrade().name()));
    }

    @Override
    public String getUsername() {
        return uuid;
    }

    @Override
    public String getPassword() {
        return null;
    }
}
