package br.com.larcash.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class ResumoDeComprasDaCateg {

	@NotBlank(message = "O nome da categoria é obrigatório")
	private String nome;
	
	@NotBlank(message = "A cor da categoria é obrigatório")
	private String cor;

	@NotNull(message = "O total de compras é obrigatório")
	@PositiveOrZero(message = "O total de compras não deve ser negativo")
	private BigDecimal total;

	@NotNull(message = "O percentual de compras é obrigatório")
	@PositiveOrZero(message = "O percentual de compras não deve ser negativo")
	private Integer percentual;

}
