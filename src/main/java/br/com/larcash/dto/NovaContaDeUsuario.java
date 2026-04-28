package br.com.larcash.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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

}
