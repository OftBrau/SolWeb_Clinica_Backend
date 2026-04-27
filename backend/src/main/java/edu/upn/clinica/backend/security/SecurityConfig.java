package edu.upn.clinica.backend.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

// ============================================================
//  SecurityConfig.java
//  Configura Spring Security para API REST stateless con JWT
//  - Sin sesiones (STATELESS)
//  - Sin CSRF (no necesario en REST)
//  - Rutas públicas: solo /api/auth/**
//  - Todo lo demás requiere token JWT válido
// ============================================================
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Sin CSRF (API REST stateless)
                .csrf(AbstractHttpConfigurer::disable)
                // Sin sesiones HTTP — cada request se autentica con JWT
                .sessionManagement(session
                        -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Reglas de acceso por endpoint
                .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                        "/api/auth/**",
                        "/v3/api-docs/**",
                        "/swagger-ui/**",
                        "/swagger-ui.html"
                ).permitAll()
                //  Público — solo login
                .requestMatchers("/api/auth/**").permitAll()
                //  Solo DOCTOR y PRACTICANTE
                .requestMatchers("/api/consultas/**")
                .hasAnyRole("DOCTOR", "PRACTICANTE")
                //  Solo DIRECTOR y ADMINISTRADOR
                .requestMatchers("/api/reportes/**")
                .hasAnyRole("DIRECTOR", "ADMINISTRADOR")
                //  Solo ADMINISTRADOR
                .requestMatchers("/api/admin/**")
                .hasRole("ADMINISTRADOR")
                // Todo lo demás requiere estar autenticado
                .anyRequest().authenticated()
                )
                // Registrar el filtro JWT antes del filtro de autenticación de Spring
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // --- BCrypt para hashear contraseñas ---
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
