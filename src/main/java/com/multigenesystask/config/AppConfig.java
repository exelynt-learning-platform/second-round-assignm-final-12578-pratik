package com.multigenesystask.config;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

public class AppConfig {

	@Autowired
    private UserDetailsService userDetailsService;

	@Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

   
    
    @Value("${app.cors.allowed-origins}")
    private String allowedOrigins;
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


    private CorsConfiguration buildCorsConfig(List<String> methods, List<String> headers) {
        CorsConfiguration config = new CorsConfiguration();

        // Split comma-separated origins, trim whitespace, reject wildcard
        List<String> origins = Arrays.stream(allowedOrigins.split(","))
                .map(String::trim)
                .filter(o -> !o.isEmpty() && !o.equals("*"))
                .collect(Collectors.toList());

        if (origins.isEmpty()) {
            throw new IllegalStateException(
                    "No valid CORS origins configured. Wildcard '*' is not permitted with credentials.");
        }

        config.setAllowedOrigins(origins);
        config.setAllowedMethods(methods);
        config.setAllowedHeaders(headers);
        config.setAllowCredentials(true);
        return config;
    }
    // ✅ CORS Configuration (SECURE)
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        CorsConfiguration apiConfig = buildCorsConfig(
            List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"),
            List.of("Authorization", "Content-Type")
        );
        List.of("/api/cart/**", "/api/orders/**", "/api/products/**",
                "/api/ratings/**", "/api/reviews/**", "/api/users/**",
                "/api/cart_items/**", "/api/admin/**")
            .forEach(path -> source.registerCorsConfiguration(path, apiConfig));

        CorsConfiguration authConfig = buildCorsConfig(
            List.of("POST", "OPTIONS"),
            List.of("Content-Type")
        );
        source.registerCorsConfiguration("/api/auth/**", authConfig);

        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource())) // ✅ ENABLE CORS
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()
                    .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/payments").permitAll()
                    .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/payments/**").authenticated()
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            );

        http.authenticationProvider(authenticationProvider());

        http.addFilterBefore(jwtAuthenticationFilter,
                UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}