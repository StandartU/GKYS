package com.example.gkys.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Primary
@Component
public class CustomAuthenticationManager implements AuthenticationManager {

    private static final Logger logger = LoggerFactory.getLogger(CustomAuthenticationManager.class);

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String login = authentication.getName();
        String password = authentication.getCredentials().toString();
        
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(login);
        logger.info(userDetails.getUsername());
        try { Thread.sleep(10000);} catch (Exception e) {}
        logger.info(userDetails.getUsername() + userDetails.getPassword());
        if (userDetails == null || !passwordEncoder.matches(password, userDetails.getPassword())) {
            throw new BadCredentialsException("Invalid login or password");
        }
        logger.info(new UsernamePasswordAuthenticationToken(userDetails, password, Collections.emptyList()).getName());

        return new UsernamePasswordAuthenticationToken(userDetails, password, Collections.emptyList());
    }
}
