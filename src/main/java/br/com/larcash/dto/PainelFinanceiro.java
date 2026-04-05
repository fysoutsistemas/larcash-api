package br.com.larcash.dto;

import java.util.ArrayList;
import java.util.List;

import br.com.larcash.entity.Lancamento;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PainelFinanceiro {
	
	@NotNull(message = "O ano é obrigatório")
	private Integer ano;
	
	@NotNull(message = "O mês é obrigatório")
	private Integer mes;
	
	@NotNull(message = "O resumo geral é obrigatório")
	private ResumoGeral resumoGeral;
	
	@NotNull(message = "Os lançamentos são obrigatórios")
	private List<Lancamento> lancamentos;
	
	public PainelFinanceiro(Integer ano, Integer mes) {
		this.ano = ano;
		this.mes = mes;		
		this.resumoGeral = new ResumoGeral();
		this.lancamentos = new ArrayList<>();
	}

}
