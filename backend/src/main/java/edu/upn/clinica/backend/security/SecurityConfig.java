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
import org.springframework.http.HttpMethod;
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
                .sessionManagement(session
                        -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Reglas de acceso por endpoint
                .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                        "/api/auth/**",
                        "/v3/api-docs/**",
                        "/api/cita-publica/**", 
                        "/swagger-ui/**",
                        "/swagger-ui.html",
                        "/ws/**",
                        "/api/farmacia/pagos/notificacion",
                        "/api/farmacia/reclamaciones/publico",
                        "/api/farmacia/ventas/checkout-public"
                ).permitAll()
                .requestMatchers("/api/admin/hce/**").hasRole("ADMINISTRADOR")
                .requestMatchers("/api/hce/**")
                .hasAnyRole("PACIENTE", "DOCTOR", "ADMINISTRADOR", "DIRECTOR", "PRACTICANTE")
                .requestMatchers("/api/citas/**").hasRole("PACIENTE")
                .requestMatchers("/api/mis-citas/**").hasRole("PACIENTE")
                .requestMatchers("/api/teleconsulta/**")
                .hasAnyRole("PACIENTE", "DOCTOR", "ADMINISTRADOR", "DIRECTOR", "PRACTICANTE")
                .requestMatchers("/api/consultas/**")
                .hasAnyRole("DOCTOR", "PRACTICANTE", "ADMINISTRADOR", "DIRECTOR")
                .requestMatchers("/api/operaciones/**")
                .hasRole("ADMINISTRATIVO")
                .requestMatchers("/api/reportes/**")
                .hasAnyRole("DIRECTOR", "ADMINISTRADOR", "ADMINISTRATIVO")
                .requestMatchers("/api/admin/**")
                .hasRole("ADMINISTRADOR")
                .requestMatchers("/api/doctores/**")
                .hasAnyRole("DOCTOR", "ADMINISTRADOR")
                .requestMatchers("/api/doctor/**")
                .hasRole("DOCTOR")
                .requestMatchers("/api/doctors/me/availability/**")
                .hasRole("DOCTOR")
                .requestMatchers("/api/doctors/me/appointments/**")
                .hasRole("DOCTOR")
                .requestMatchers("/api/patients/me/appointments/**")
                .hasRole("PACIENTE")
                .requestMatchers("/api/doctors").hasAnyRole("PACIENTE", "DOCTOR", "ADMINISTRADOR")
                .requestMatchers("/api/doctors/*/available-dates").hasAnyRole("PACIENTE", "DOCTOR")
                .requestMatchers("/api/doctors/*/available-slots").hasAnyRole("PACIENTE", "DOCTOR")
                .requestMatchers("/api/appointments").hasRole("PACIENTE")
                .requestMatchers("/api/examenes/**")
                .hasAnyRole("DOCTOR", "ADMINISTRADOR")
                .requestMatchers("/api/practicante/invitaciones/doctor", "/api/practicante/invitaciones").hasAnyRole("DOCTOR", "MEDICO")
                .requestMatchers("/api/practicante/invitaciones/{id}/aceptar", "/api/practicante/invitaciones/{id}/rechazar").hasRole("PRACTICANTE")
                .requestMatchers("/api/practicante/mis-invitaciones/**").hasRole("PRACTICANTE")
                .requestMatchers("/api/practicante/**")
                .hasAnyRole("PRACTICANTE", "DOCTOR", "ADMINISTRADOR", "DIRECTOR")
                .requestMatchers(HttpMethod.GET, "/api/farmacia/medicamentos/**").permitAll()
                .requestMatchers("/api/farmacia/medicamentos/**")
                .hasAnyRole("PACIENTE", "DOCTOR", "ADMINISTRADOR", "ADMINISTRATIVO", "DIRECTOR")
                .requestMatchers("/api/farmacia/ventas/**")
                .hasAnyRole("PACIENTE", "ADMINISTRADOR", "ADMINISTRATIVO")
                .requestMatchers("/api/farmacia/carrito/**")
                .hasAnyRole("PACIENTE", "ADMINISTRADOR", "PRACTICANTE")
                .requestMatchers("/api/farmacia/reclamaciones/{id}/responder", "/api/farmacia/reclamaciones")
                .hasAnyRole("ADMINISTRADOR", "ADMINISTRATIVO")
                .requestMatchers("/api/farmacia/reclamaciones/mis-reclamaciones")
                .hasRole("PACIENTE")
                .requestMatchers("/api/farmacia/reclamaciones/{id}")
                .hasAnyRole("PACIENTE", "ADMINISTRADOR", "ADMINISTRATIVO")
                .requestMatchers("/api/farmacia/pagos/**")
                .hasAnyRole("PACIENTE", "ADMINISTRADOR", "ADMINISTRATIVO")
                .requestMatchers("/api/perfil/foto").authenticated()
                .requestMatchers("/api/perfil/profesional/**").hasAnyRole("PRACTICANTE", "DOCTOR", "ADMINISTRADOR", "DIRECTOR")
                .requestMatchers("/api/perfil/practicantes").hasAnyRole("DOCTOR", "ADMINISTRADOR", "DIRECTOR")
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
