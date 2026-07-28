package br.com.larcash.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class ItemDoCarrinho {

	@NotNull(message = "O id do produto é obrigatório")
	@Positive(message = "O id do produto deve ser positivo")
	private Integer idDoProduto;
	
	@NotNull(message = "A qtde é obrigatória")
	@Positive(message = "A qtde deve ser positiva")
	private BigDecimal qtde;
	
	@NotNull(message = "O preço é obrigatório")
	@Positive(message = "O preço deve ser positivo")
	private BigDecimal preco;
	
}
