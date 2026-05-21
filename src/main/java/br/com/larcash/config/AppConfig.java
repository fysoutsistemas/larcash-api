package br.com.larcash.config;

import java.util.TimeZone;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
		Hibernate5JakartaModule hibernate5Module = new Hibernate5JakartaModule();
		hibernate5Module.disable(Hibernate5JakartaModule.Feature.USE_TRANSIENT_ANNOTATION);
        return hibernate5Module;
    }
	
}
