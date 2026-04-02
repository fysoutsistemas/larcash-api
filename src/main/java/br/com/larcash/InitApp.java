package br.com.larcash;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class InitApp {
	
	public static void main(String[] args) {
		SpringApplication.run(InitApp.class, args);
	}	
	
	@Bean
	public CommandLineRunner executar(ApplicationContext ctx) {
		return args -> {
			
		};
	}
	
}
