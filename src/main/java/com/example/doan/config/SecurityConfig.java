package com.example.doan.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.example.doan.Model.JwtUtil;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig  {
    @Autowired 
    private JwtUtil jwtUtil;
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.cors().and().csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authozire -> authozire
                                .requestMatchers("/admin/**").hasRole("ADMIN")
                                .requestMatchers("/user/login").permitAll()
                                .requestMatchers("/game/*").permitAll()
                                // .requestMatchers("/Atm/search").permitAll()
                                .anyRequest().permitAll()
                                // .anyRequest().authenticated()
                )
                .addFilterBefore(jwtUtil, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

}
