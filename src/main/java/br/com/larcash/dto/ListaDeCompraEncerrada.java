package br.com.larcash.dto;

import br.com.larcash.enums.Confirmacao;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class ListaDeCompraEncerrada {

	@NotNull(message = "O id da lista é obrigatório")
	@Positive(message = "O id da lista deve ser positivo")
	private Integer id;
	
	@NotNull(message = "O indicador de lançamento de despesa")
	private Confirmacao flagLancarDespesa;
	
	@NotBlank(message = "O login do comprador é obrigatório")
	private String loginDoComprador;
	
}
