package br.com.larcash.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ResumoGeral {
	
	@NotNull(message = "O total gasto é obrigatório")
	private BigDecimal totalGasto;
	
	@NotNull(message = "O total orçado é obrigatório")
	private BigDecimal totalOrcado;
	
	@NotNull(message = "O percentual gasto é obrigatório")
	private Integer percentualGasto;
	
	@NotNull(message = "Os resumos por categoria são obrigatórios")
	private List<ResumoPorCategoria> resumosPorCategoria;
	
	public ResumoGeral() {
		this.totalGasto = new BigDecimal(0.0);
		this.totalOrcado = new BigDecimal(0.0);
		this.percentualGasto = 0;
		this.resumosPorCategoria = new ArrayList<>();
	}

}
