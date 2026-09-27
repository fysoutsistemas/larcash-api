package br.com.larcash.dto;

import br.com.larcash.enums.Confirmacao;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
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
	
	@NotNull(message = "A qtde de membros é obrigatória")
	@PositiveOrZero(message = "A qtde de membros não pode ser negativa")
	private Integer qtdeDeMembros;
	
}
