package br.com.larcash.dto.indicadores;

import java.util.ArrayList;
import java.util.List;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class IndicadoresGerais {
	
	@NotNull(message = "O ano é obrigatório")
	@Positive(message = "O ano deve ser positivo")
	@Max(value = 2100, message = "O ano é inválido")
	private Integer ano;
	
	@NotNull(message = "O mês é obrigatório")
	@Positive(message = "O mês deve ser positivo")
	@Min(value = 1, message = "O mês deve estar entre 1 e 12")
	@Max(value = 12, message = "O mês deve estar entre 1 e 12")
	private Integer mes;
	
	@NotNull(message = "O resumo geral do orçamento é obrigatório")
	private List<ResumoGeralDeOrc> resumosGeraisDeOrc;
	
	@NotNull(message = "Os gastos por categoria")
	private List<GastoOrcPorCateg> gastosOrcPorCategs;
	
	@NotNull(message = "O maior gasto do orçamento é obrigatório")
	private MaiorGastoDoOrc maiorGastoDoOrc;
	
	@NotNull(message = "A economia em compras é obrigatória")
	private EconomiaEmCompras economiaEmCompras;
	
	@NotNull(message = "O resumo geral de compras é obrigatório")
	private ResumoGeralDeCompras resumoGeralDeCompras;	
	
	@NotNull(message = "Os maiores gastos em produtos é obrigatório")
	private List<MaiorGastoDeProduto> maioresGastosDeProd;
	
	public IndicadoresGerais() {
		this.ano = 0;
		this.mes = 0;
		this.resumosGeraisDeOrc = new ArrayList<>();
		this.economiaEmCompras = new EconomiaEmCompras();
		this.resumoGeralDeCompras = new ResumoGeralDeCompras();
		this.gastosOrcPorCategs = new ArrayList<>();
		this.maiorGastoDoOrc = new MaiorGastoDoOrc();
		this.maioresGastosDeProd = new ArrayList<>();
	}
	
}
