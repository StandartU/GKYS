package com.example.gkys.security;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.gkys.model.UserModel;

public class CustomUserDetailsModel extends UserModel implements UserDetails{

    private String login;
    private String password;

    public CustomUserDetailsModel(UserModel userModel) {
        this.login = userModel.getLogin();
        this.password = userModel.getPassword();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    @Override
    public String getUsername() {
        return login;
    }

    public String getPassword() {
        return password;
    }
}
