package br.com.larcash.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        // 1. Define origens explícitas ou padrão com wildcard 
        // (NUNCA use apenas "*" se enviar tokens/credentials)
        config.setAllowedOriginPatterns(List.of(
            "https://larcash.com.br",
            "https://www.larcash.com.br",
            "http://localhost:*",
            "http://192.168.100.55:*",
            "http://192.168.7.10:*"
        ));

        // 2. Permite todos os métodos HTTP (incluindo OPTIONS para preflight)
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));

        // 3. Permite todos os cabeçalhos solicitados pelo frontend (ex: Authorization, Content-Type)
        config.setAllowedHeaders(List.of("*"));

        // 4. Expõe o cabeçalho Authorization se o frontend precisar lê-lo
        config.setExposedHeaders(List.of("*"));

        // 5. Permite credenciais/tokens
        config.setAllowCredentials(true);

        // 6. Define tempo de cache do preflight (1 hora)
        config.setMaxAge(3600L);

        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}