package com.api.readinglog.common.security;

import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.toH2Console;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    return http
        .httpBasic(httpBasic -> httpBasic.disable())
        .csrf(csrf -> csrf.disable())
        .cors(cors -> cors.disable())
        .formLogin(formLogin -> formLogin.disable())
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/**").permitAll()
        )
        .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
        .logout(logout -> logout.logoutSuccessUrl("/api/members/logout"))
        .build();
        //.oauth2Login(oauth2 -> oauth2.loginPage()) TODO: implement social login
        //.addFilterBefore(, UsernamePasswordAuthenticationFilter.class) TODO: implement JWT

  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return PasswordEncoderFactories.createDelegatingPasswordEncoder();
  }

}
