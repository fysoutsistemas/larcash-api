package br.com.larcash.util;

import org.springframework.stereotype.Component;

import jakarta.validation.constraints.NotBlank;

@Component
public class PhoneUtil {

	public String removerMascaraDo(
			@NotBlank(message = "O número de telefone é obrigatório")
			String numero) {
		return numero.replace("(", "").replace(")", "")
				.replace(" ", "").replace("-", "");
	}
	
}
