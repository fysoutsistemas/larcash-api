package br.com.larcash.dto;

import java.math.BigDecimal;

import br.com.larcash.enums.Status;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class NovoLimiteDaCategoria {
	
	@EqualsAndHashCode.Include
	@NotNull(message = "O id da categoria é obrigatório")
	@Positive(message = "O id da categoria deve ser positivo")
	private Integer idDaCategoria;
	
	@NotNull(message = "O valor do limite é obrigatório")
	private BigDecimal valor;
		
	private Status status;
	
	public NovoLimiteDaCategoria() {
		this.status = Status.A;
	}

}
