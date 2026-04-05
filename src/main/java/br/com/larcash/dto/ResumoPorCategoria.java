package br.com.larcash.dto;

import java.math.BigDecimal;

import br.com.larcash.entity.Categoria;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ResumoPorCategoria {
	
	@NotNull(message = "A categoria é obrigatória")
	@EqualsAndHashCode.Include
	private Categoria categoria;
	
	@NotNull(message = "Os gastos são obrigatórios")
	private BigDecimal gastos;
	
	@NotNull(message = "O percentual é obrigatório")
	private Integer percentual;
	
	public ResumoPorCategoria(Categoria categoria) {
		this.categoria = categoria;
		this.gastos = new BigDecimal(0.0);
		this.percentual = 0;
	}
	
}
