package com.pos_billing.pos_backend.config;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        return http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(request -> {
                    CorsConfiguration config = new CorsConfiguration();
                    config.setAllowedOriginPatterns(List.of("http://localhost:5173",
                        "https://pos-by-kashif.vercel.app"
                    ));
                    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                    config.setAllowedHeaders(List.of("*"));
                    config.setAllowCredentials(true);
                    return config;
                }))

                .authorizeHttpRequests(auth -> auth
                        // ---------- PUBLIC ----------
                        .requestMatchers("/api/users/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/payment/create").permitAll()
                        .requestMatchers("/health").permitAll()

                        // ---------- CUSTOMER ----------
                        .requestMatchers(HttpMethod.POST, "/api/payment/verify-and-create").hasRole("CUSTOMER")
                        .requestMatchers(HttpMethod.POST, "/api/orders/create").hasRole("CUSTOMER")
                        .requestMatchers("/api/customer/**").hasRole("CUSTOMER")

                        // ---------- ADMIN ----------
                        .requestMatchers(HttpMethod.GET, "/api/orders").hasRole("ADMIN")
                        .requestMatchers("/api/order-items/**").hasRole("ADMIN")
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        // ---------- OTHERS ----------
                        .anyRequest().permitAll())

                // BASIC AUTH (for Postman testing)
                .httpBasic(httpBasic -> {
                })

                // Enable user details service
                .userDetailsService(customUserDetailsService)

                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
