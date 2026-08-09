package br.com.larcash.dto;

import br.com.larcash.enums.Confirmacao;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ResumoDaContaDeUsuario {
	
	@NotBlank(message = "O login é obrigatório")
	private String login;
	
	@NotBlank(message = "O nome completo é obrigatório")
	private String nomeCompleto;
	
	@NotBlank(message = "O nome da família é obrigatório")
	private String nomeDaFamilia;
	
	@NotNull(message = "O indicador de configuração de categorias é obrigatório")
	private Confirmacao flCategoriasConfiguradas;
	
	@NotNull(message = "O indicador de chefe de familia é obrigatório")
	private Confirmacao flChefeDaFamilia;
	
	public String foto;
	
}
