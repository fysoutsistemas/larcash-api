package br.com.larcash.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class DashboardDeCompras {

	@NotNull(message = "O total comprado é obrigatório")
	@PositiveOrZero(message = "O total comprado não deve ser negativo")
	private BigDecimal totalComprado;
	
	@NotNull(message = "O total de listas é obrigatório")
	@PositiveOrZero(message = "O total de listas não deve ser negativo")
	private Integer totalDeListas;
	
	@NotNull(message = "O período é obrigatório")
	@PositiveOrZero(message = "O período não deve ser negativo")
	private Integer periodoEmDias;
	
	@NotNull(message = "Listagem de resumos é obrigatória")
	private List<ResumoDeComprasDaCateg> resumosPorCateg;
	
	public DashboardDeCompras() {
		this.resumosPorCateg = new ArrayList<>();
	}
	
}
