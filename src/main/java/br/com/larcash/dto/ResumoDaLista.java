package br.com.larcash.dto;

import br.com.larcash.enums.StatusDaLista;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResumoDaLista {
	
	@NotNull(message = "O status é obrigatório")
	private StatusDaLista status;
	
	@NotNull(message = "A quantidade é obrigatória")
	@PositiveOrZero(message = "A quantidade não pode ser negativa")
	private Long qtde;
	
}
