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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

// ============================================================
//  SecurityConfig.java  —  edu.upn.clinica.backend.security
//  Cambios respecto a la versión anterior:
//    + .cors() apuntando al bean corsConfigurationSource()
//    + Bean CorsConfigurationSource que permite localhost:4200
// ============================================================
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // CORS — debe ir ANTES que csrf y session
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // Sin CSRF (API REST stateless)
                .csrf(AbstractHttpConfigurer::disable)

                // Sin sesiones HTTP — cada request se autentica con JWT
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Reglas de acceso por endpoint
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/auth/**",
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html"
                        ).permitAll()
                        .requestMatchers("/api/consultas/**")
                                .hasAnyRole("DOCTOR", "PRACTICANTE")
                        .requestMatchers("/api/reportes/**")
                                .hasAnyRole("DIRECTOR", "ADMINISTRADOR")
                        .requestMatchers("/api/admin/**")
                                .hasRole("ADMINISTRADOR")
                        .anyRequest().authenticated()
                )

                // Filtro JWT
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // ----------------------------------------------------------------
    //  Configuración CORS — permite peticiones desde Angular en dev
    // ----------------------------------------------------------------
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOrigins(List.of(
                "http://localhost:4200",
                "http://localhost:4201"
        ));

        config.setAllowedMethods(List.of(
                "GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"
        ));

        // Permite todos los headers de la petición (incluido Authorization)
        config.setAllowedHeaders(List.of("*"));

        // Expone Authorization para que Angular pueda leerlo si lo necesita
        config.setExposedHeaders(List.of("Authorization"));

        // Necesario para enviar el header Authorization con credenciales
        config.setAllowCredentials(true);

        // Cachea el preflight 1 hora — reduce peticiones OPTIONS repetidas
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    // --- BCrypt para hashear contraseñas ---
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}