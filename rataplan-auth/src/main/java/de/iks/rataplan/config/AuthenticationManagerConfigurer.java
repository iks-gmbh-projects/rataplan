package de.iks.rataplan.config;

import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

@RequiredArgsConstructor
public class AuthenticationManagerConfigurer extends AbstractHttpConfigurer<AuthenticationManagerConfigurer, HttpSecurity> {
    private final AbstractAuthenticationProcessingFilter filter;
    @Override
    public void configure(HttpSecurity builder) {
        filter.setAuthenticationManager(builder.getSharedObject(AuthenticationManager.class));
    }
}