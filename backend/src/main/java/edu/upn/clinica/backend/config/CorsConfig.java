package edu.upn.clinica.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

// ============================================================
//  CorsConfig.java
//  Permite peticiones desde el frontend Angular en desarrollo
// ============================================================
@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();

        // Orígenes permitidos (agregar producción cuando sea necesario)
        config.setAllowedOrigins(List.of(
            "http://localhost:4200",
            "http://localhost:4201"
        ));

        // Métodos HTTP permitidos
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));

        // Headers permitidos en la petición
        config.setAllowedHeaders(List.of("*"));

        // Exponer el header Authorization para que Angular pueda leerlo
        config.setExposedHeaders(List.of("Authorization"));

        // Permitir cookies / credenciales
        config.setAllowCredentials(true);

        // Cachear el preflight por 1 hora (3600 segundos)
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}