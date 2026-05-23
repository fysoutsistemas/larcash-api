package br.com.larcash.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class NovoMembro {

	@NotBlank(message = "O login é obrigatório")
	private String login;
	
	@NotBlank(message = "A senha é obrigatória")
	private String senha;
	
	@NotBlank(message = "O nome completo é obrigatório")
	private String nomeCompleto;
	
	@NotBlank(message = "O token do convite é obrigatório")
	private String tokenDoConvite;
	
}
