package br.com.larcash.dto.indicadores;

import br.com.larcash.enums.StatusDoOrc;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DiagnosticoDoOrc {
	
	@NotBlank(message = "O resumo é obrigatório")
	private String resumo;
	
	@NotBlank(message = "O detalhamento é obrigatório")
	private String detalhamento;
	
	@NotNull(message = "O status é obrigatório")
	private StatusDoOrc status;
	
}
