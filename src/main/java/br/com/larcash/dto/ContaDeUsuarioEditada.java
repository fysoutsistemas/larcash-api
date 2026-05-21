package br.com.larcash.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ContaDeUsuarioEditada {

	@EqualsAndHashCode.Include
	@NotBlank(message = "O login é obrigatório")
	private String login;
	
	@NotBlank(message = "O nome completo é obrigatório")
	private String nomeCompleto;
	
	@NotBlank(message = "O nome da família é obrigatório")
	private String nomeDaFamilia;

	private String senhaAtual;

	private String novaSenha;
	
}
