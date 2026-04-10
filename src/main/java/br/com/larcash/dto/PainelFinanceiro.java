package br.com.larcash.dto;

import java.util.ArrayList;
import java.util.List;

import br.com.larcash.entity.Lancamento;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PainelFinanceiro {
	
	@NotNull(message = "O resumo geral é obrigatório")
	private ResumoGeral resumoGeral;
	
	@NotNull(message = "Os lançamentos são obrigatórios")
	private List<Lancamento> lancamentos;
	
	public PainelFinanceiro() {
		this.resumoGeral = new ResumoGeral();
		this.lancamentos = new ArrayList<>();
	}

}
