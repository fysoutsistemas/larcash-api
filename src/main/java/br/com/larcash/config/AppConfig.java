package br.com.larcash.config;

import java.util.Arrays;
import java.util.TimeZone;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.fasterxml.jackson.datatype.hibernate5.jakarta.Hibernate5JakartaModule;

import jakarta.annotation.PostConstruct;

@Configuration
public class AppConfig {	

	@PostConstruct
	public void inicializar() {
		TimeZone.setDefault(TimeZone.getTimeZone("America/Sao_Paulo"));
	}
	
	@Bean
    public Hibernate5JakartaModule jsonHibernate5Module() {
        return new Hibernate5JakartaModule();
    }
	
	@Configuration
	public class CorsConfig {
	    @Bean
	    public CorsConfigurationSource corsConfigurationSource() {
	    	CorsConfiguration corsConfiguration = new CorsConfiguration();
		    corsConfiguration.applyPermitDefaultValues(); 
		    corsConfiguration.setAllowedHeaders(Arrays.asList("*"));
		    corsConfiguration.setAllowedMethods(Arrays.asList("*"));
		    corsConfiguration.setAllowedOrigins(Arrays.asList("*"));
		    corsConfiguration.setExposedHeaders(Arrays.asList("*"));
		    UrlBasedCorsConfigurationSource ccs = new UrlBasedCorsConfigurationSource();
		    ccs.registerCorsConfiguration("/**", corsConfiguration);
		    return ccs;
	    }
	}

}
