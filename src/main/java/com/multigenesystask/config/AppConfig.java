package com.multigenesystask.config;

import java.util.List;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.multigenesystask.config.jwt.JwtAuthenticationFilter;

import lombok.AllArgsConstructor;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@AllArgsConstructor
public class AppConfig {

    private UserDetailsService userDetailsService;

    // ✅ JWT Filter
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter();
    }

    // ✅ Password Encoder
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // ✅ Authentication Provider
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }

    // ✅ Authentication Manager
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    // ✅ CORS Configuration (SECURE)
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        // ── 1. Frontend API routes (/api/** except payments and auth) ──────────
        CorsConfiguration apiConfig = new CorsConfiguration();
        apiConfig.setAllowedOrigins(List.of("http://localhost:4200"));
        apiConfig.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        apiConfig.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        apiConfig.setAllowCredentials(true);
        source.registerCorsConfiguration("/api/cart/**",       apiConfig);
        source.registerCorsConfiguration("/api/orders/**",     apiConfig);
        source.registerCorsConfiguration("/api/products/**",   apiConfig);
        source.registerCorsConfiguration("/api/ratings/**",    apiConfig);
        source.registerCorsConfiguration("/api/reviews/**",    apiConfig);
        source.registerCorsConfiguration("/api/users/**",      apiConfig);
        source.registerCorsConfiguration("/api/cart_items/**", apiConfig);
        source.registerCorsConfiguration("/api/admin/**",      apiConfig);

        // ── 2. Auth routes — no Authorization header needed for login/register ─
        CorsConfiguration authConfig = new CorsConfiguration();
        authConfig.setAllowedOrigins(List.of("http://localhost:4200"));
        authConfig.setAllowedMethods(List.of("POST", "OPTIONS"));
        authConfig.setAllowedHeaders(List.of("Content-Type")); // ← no Authorization
        authConfig.setAllowCredentials(true);
        source.registerCorsConfiguration("/api/auth/**", authConfig);

        // ── 3. Payment callback — Razorpay server-to-server GET redirect ───────
        // No CORS headers at all. Browser never calls this directly.
        // Razorpay redirects the user's browser here after payment — no preflight.
        // Intentionally not registered → no CORS config = no CORS headers sent.

        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource())) // ✅ ENABLE CORS
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/payments").permitAll() 
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            );

        http.authenticationProvider(authenticationProvider());

        http.addFilterBefore(jwtAuthenticationFilter(),
                UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}