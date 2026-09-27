package br.com.larcash.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SenhaResetada {
	
	@NotBlank(message = "O login é obrigatório")
	private String login;
	
	@NotBlank(message = "O código OTP é obrigatório")
	private String codigoOTP;
	
	@NotBlank(message = "A nova senha é obrigatória")
	private String novaSenha;
	
}
