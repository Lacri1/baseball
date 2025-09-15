package com.baseball.game.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
                .antMatchers("/h2-console/**", "/api/**").permitAll()
                .anyRequest().permitAll()
            .and()
            .csrf()
                .ignoringAntMatchers("/h2-console/**", "/api/**")
            .and()
            .headers()
                .frameOptions().sameOrigin();
        
        return http.build();
    }
}