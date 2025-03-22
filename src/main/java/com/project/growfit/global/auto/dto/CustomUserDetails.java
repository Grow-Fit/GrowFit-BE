package com.project.growfit.global.auto.dto;

import com.project.growfit.domain.User.entity.Child;
import com.project.growfit.domain.User.entity.Parent;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
public class CustomUserDetails implements UserDetails {

    private String userId;
    private String userPassword;
    private String email;
    private String nickname;
    private String role;


    public CustomUserDetails(Parent parent) {
        this.userId = parent.getEmail();
        this.email = parent.getEmail();
        this.nickname = parent.getNickname();
        this.role = "ROLE_PARENT";
    }

    public CustomUserDetails(Child child) {
        this.userId = child.userId();
        this.userPassword = child.user_password();
        this.email = null;
        this.nickname = child.userNickname();
        this.role = "ROLE_CHILD";
    }
    public CustomUserDetails(Object user) {
        if (user instanceof Parent parent) {
            this.userId = parent.getEmail();
            this.email = parent.getEmail();
            this.nickname = parent.getNickname();
            this.role = "ROLE_PARENT";
        } else if (user instanceof Child child) {
            this.userId = child.userId();
            this.userPassword = child.user_password();
            this.email = null;
            this.nickname = child.userNickname();
            this.role = "ROLE_CHILD";
        } else {
            throw new IllegalArgumentException("Invalid user type: " + user.getClass().getSimpleName());
        }
    }

    // 토큰 재발급용
    public CustomUserDetails(String userId, String role) {
        this.userId = userId;
        this.email = null;
        this.nickname = null;
        this.role = role;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role));
    }

    @Override
    public String getPassword() {
        return userPassword;
    }

    @Override
    public String getUsername() {
        return userId;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public String user_role() {
        return role;
    }
}