package de.iks.rataplan.config;

import de.iks.rataplan.service.JwtTokenService;
import de.iks.rataplan.service.RataplanUserDetails;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.io.PrintWriter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig {
    private final FrontendConfig frontendConfig;
    
    @Bean
    public SecurityFilterChain normalSecurity(HttpSecurity httpSecurity, JwtTokenService jwtTokenService) throws
        Exception
    {
        UsernamePasswordAuthenticationFilter usernamePasswordAuthenticationFilter =
            new UsernamePasswordAuthenticationFilter();
        usernamePasswordAuthenticationFilter.setAuthenticationSuccessHandler((request, response, authentication) -> {
            RataplanUserDetails userDetails = (RataplanUserDetails) authentication.getPrincipal();
            response.setStatus(200);
            PrintWriter writer = response.getWriter();
            writer.println(jwtTokenService.generateLoginToken(userDetails));
            writer.close();
        });
        usernamePasswordAuthenticationFilter.setAuthenticationFailureHandler((request, response, exception) -> response.sendError(
            (exception instanceof DisabledException ? HttpStatus.FORBIDDEN : HttpStatus.UNAUTHORIZED).value())
        );
        return httpSecurity.cors(Customizer.withDefaults())
            .csrf(CsrfConfigurer::disable)
            .addFilter(usernamePasswordAuthenticationFilter)
            .apply(new AuthenticationManagerConfigurer(usernamePasswordAuthenticationFilter))
            .and()
            .oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt)
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(r -> {
                r.mvcMatchers("/issuer/**").permitAll();
                r.mvcMatchers(
                    "/v1/users/register",
                    "/v1/feedback",
                    "/v1/users/mailExists",
                    "/v1/users/usernameExists",
                    "/v1/users/displayName/**",
                    "/v1/users/forgotPassword",
                    "/v1/resend-confirmation-email",
                    "/v1/notifications/list-settings"
                ).permitAll();
                r.mvcMatchers("/userid", "/notification").hasAuthority("SCOPE_" + JwtTokenService.SCOPE_ID);
                r.mvcMatchers("/v1/users/resetPassword").hasAuthority("SCOPE_" + JwtTokenService.SCOPE_RESET_PASSWORD);
                r.mvcMatchers("/v1/confirm-account")
                    .hasAuthority("SCOPE_" + JwtTokenService.SCOPE_ACCOUNT_CONFIRMATION);
                r.mvcMatchers("/v1/**").hasAuthority("SCOPE_" + JwtTokenService.SCOPE_LOGIN);
                r.anyRequest().authenticated();
            })
            .build();
    }
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin(frontendConfig.getUrl());
        config.applyPermitDefaultValues();
        config.addAllowedMethod(HttpMethod.DELETE);
        config.addAllowedMethod(HttpMethod.PUT);
        return ignored -> config;
    }
}