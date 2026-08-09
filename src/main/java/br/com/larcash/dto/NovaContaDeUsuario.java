package br.com.larcash.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class NovaContaDeUsuario {
	
	@NotBlank(message = "O login é obrigatório")
	private String login;
	
	@NotBlank(message = "A senha é obrigatória")
	private String senha;
	
	@NotBlank(message = "O nome completo é obrigatório")
	private String nomeCompleto;
	
	@NotBlank(message = "O nome da família é obrigatório")
	private String nomeDaFamilia;
	
	@NotNull(message = "O orçamento mensal é obrigatório")
	@Positive(message = "O orçamento mensal deve ser positivo")
	private BigDecimal orcamentoMensal;
	
	@NotBlank(message = "O telefone é obrigatório")	
	@Size(max = 20, message = "O telefone não deve conter mais de 20 caracteres")
	private String telefone;

}
