package com.example.hotel_booking.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())

                .authorizeHttpRequests(auth -> auth
                        // LOGIN & REGISTER
                        .requestMatchers("/api/v1/auth/**").permitAll()

                        // PUBLIC ENDPOINTS (GUEST FLOW)
                        .requestMatchers(HttpMethod.POST, "/api/v1/bookings").permitAll()
                        .requestMatchers(
                                "/api/v1/customers/phone/**",
                                "/api/v1/bookings/by-code/**",
                                "/api/v1/bookings/lookup/**",
                                "/api/v1/bookings/*/cancel-full",
                                "/api/v1/bookings/*/cancel-room/**",
                                "/api/v1/roomtype/**",
                                "/api/v1/roomtype/available",
                                "/api/v1/rooms/**",
                                "/api/v1/services/**",
                                "/api/v1/auth/**",
                                "/uploads/**",
                                "/api/v1/payments/vnpay-callback",
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/error"
                        ).permitAll()

                        .requestMatchers("/api/v1/users/**").authenticated()
                        .requestMatchers("/api/v1/bookings/my-history").authenticated()

                        // ADMIN SECURED ENDPOINTS
                        .requestMatchers("/api/v1/bookings/**").hasAnyRole("ADMIN", "RECEPTIONIST")
                        .requestMatchers("/api/v1/booking-rooms/**").hasAnyRole("ADMIN", "RECEPTIONIST")
                        .requestMatchers("/api/v1/booking-service-details/**").hasAnyRole("ADMIN", "RECEPTIONIST")
                        .requestMatchers("/api/v1/customers/**").hasAnyRole("ADMIN", "RECEPTIONIST")
                        .requestMatchers("/api/v1/rooms/**").hasAnyRole("ADMIN", "RECEPTIONIST")
                        .requestMatchers("/api/v1/services/**").hasAnyRole("ADMIN", "RECEPTIONIST")
                        .requestMatchers("/api/v1/customers/**").hasAnyRole("ADMIN", "RECEPTIONIST")
                        .requestMatchers("/api/v1/audit-logs/**").hasAnyRole("ADMIN", "RECEPTIONIST")
                        .requestMatchers("/api/v1/dashboard/**").hasAnyRole("ADMIN", "RECEPTIONIST")

                        .anyRequest().authenticated()
                )

                .exceptionHandling(exception -> exception
                        .accessDeniedHandler(customAccessDeniedHandler)
                        .authenticationEntryPoint(customAuthenticationEntryPoint)
                )

                .sessionManagement(sess ->
                        sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                .authenticationProvider(authenticationProvider)

                .addFilterBefore(
                        jwtAuthFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(
                List.of("http://localhost:5173", "http://127.0.0.1:5173")
        );

        configuration.setAllowedMethods(
                List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
        );

        // Chi tiết hóa Header cụ thể thay vì dùng "*" bừa bãi
        configuration.setAllowedHeaders(
                List.of("Authorization", "Content-Type", "Cache-Control", "Accept", "X-Requested-With", "Origin")
        );

        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}