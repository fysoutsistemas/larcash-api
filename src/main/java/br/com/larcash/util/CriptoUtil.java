package br.com.larcash.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class CriptoUtil {

	public String criptografar(String senhaPlana) {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		return encoder.encode(senhaPlana);
	}
	
}
