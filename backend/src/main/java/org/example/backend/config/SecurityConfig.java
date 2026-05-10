package org.example.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(
            HttpSecurity http
    ) throws Exception {

        http
                .csrf(csrf -> csrf.disable())

                .authorizeHttpRequests(auth -> auth

                        .requestMatchers(
                                "/",
                                "/index.html",
                                "/favicon.ico",
                                "/coin.proto",
                                "/login",
                                "/callback",
                                "/logout",
                                "/user-info",
                                "/debug-cookie",
                                "/ws/**"
                        ).permitAll()

                        .anyRequest().authenticated()
                );

        return http.build();
    }
}