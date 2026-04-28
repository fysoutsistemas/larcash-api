package br.com.larcash.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProgressoDoOrcamento {

	@NotNull(message = "O total orçado é obrigatório")
	private BigDecimal totalOrcado;
	
	@NotNull(message = "O total gasto é obrigatório")
	private BigDecimal totalGasto;	
	
	@NotNull(message = "O percentual gasto é obrigatório")
	private Integer percentualGasto;
	
	@NotNull(message = "O total restante é obrigatório")
	private BigDecimal totalRestante;	
	
}
