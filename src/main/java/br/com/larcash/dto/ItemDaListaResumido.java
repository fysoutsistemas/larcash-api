package br.com.larcash.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class ItemDaListaResumido {
	
	@NotNull(message = "O id do produto é obrigatório")
	@Positive(message = "O id do produto deve ser positivo")
	private Integer idDoProduto;
	
	@NotNull(message = "A qtde é obrigatória")
	@Positive(message = "A qtde deve ser positiva")
	private BigDecimal qtde;
	
	@NotNull(message = "A ordem é obrigatória")
	@Positive(message = "A ordem deve ser positiva")
	private Integer ordem;

}
