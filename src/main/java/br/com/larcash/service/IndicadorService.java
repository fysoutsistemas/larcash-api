package br.com.larcash.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.google.common.base.Preconditions;

import br.com.larcash.config.validation.anotacao.IdValido;
import br.com.larcash.config.validation.anotacao.MesValido;
import br.com.larcash.dto.indicadores.DiagnosticoDoOrc;
import br.com.larcash.dto.indicadores.EconomiaEmCompras;
import br.com.larcash.dto.indicadores.GastoOrcPorCateg;
import br.com.larcash.dto.indicadores.IndicadoresGerais;
import br.com.larcash.dto.indicadores.MaiorGastoDeProduto;
import br.com.larcash.dto.indicadores.MaiorGastoDoOrc;
import br.com.larcash.dto.indicadores.ResumoGeralDeCompras;
import br.com.larcash.dto.indicadores.ResumoGeralDeOrc;
import br.com.larcash.entity.Orcamento;
import br.com.larcash.enums.StatusDoOrc;
import br.com.larcash.repository.projection.GastoPorCategoria;
import br.com.larcash.repository.projection.ResumoDeCompraDoProd;
import br.com.larcash.repository.projection.TotaisDaCompra;
import br.com.larcash.repository.projection.TotaisDoOrcamento;
import jakarta.validation.constraints.PositiveOrZero;

@Service
@Validated
public class IndicadorService {
	
	@Autowired
	private OrcamentoService orcService;
	
	@Autowired
	private LanctoService lanctoService;
	
	@Autowired
	private ListaDeCompraService listaService;
	
	public IndicadoresGerais buscarIndicadores(
			@IdValido(nomeDoAtributo = "id da família")
			Integer idDaFamilia,
			@PositiveOrZero(message = "O ano é obrigatório e não dever ser negativo")
			Integer ano,
			@MesValido(nomeDoAtributo = "mês")
			Integer mes) {
		
		List<TotaisDoOrcamento> totalizadores = orcService.listarTotalizadoresPor(
				idDaFamilia, Pageable.ofSize(12));
		
		List<ResumoGeralDeOrc> resumos = new ArrayList<>();
		
		for (TotaisDoOrcamento totais : totalizadores) {
			
			ResumoGeralDeOrc resumo = new ResumoGeralDeOrc();
			
			resumo.setIdDoOrcamento(totais.getIdDoOrcamento());
			
			resumo.setTotalGasto(totais.getTotalGasto()
					.setScale(2, RoundingMode.HALF_EVEN));
			
			resumo.setPercGasto(totais.getPercGasto());
			
			resumo.setTotalDisponivel(totais.getTotalDisponivel()
					.setScale(2, RoundingMode.HALF_EVEN));
			
			resumo.setLimite(totais.getLimite().setScale(
					2, RoundingMode.HALF_EVEN));
			
			resumo.setMediaDeGastoDia(totais.getMediaDeGastoDia()
					.setScale(2, RoundingMode.HALF_EVEN));
			
			resumo.setDataDeTermino(totais.getDataDeTermino());
			
			Integer percGasto = totais.getPercGasto();
			
			if (totais.getTotalGasto().doubleValue() > 0 && totais.getPercGasto() == 0) {
				resumo.setPercGasto(1);
				percGasto = 1;				
			}
			
			DiagnosticoDoOrc diagnostico = new DiagnosticoDoOrc();
			DecimalFormat df = new DecimalFormat("#,##0.00", new DecimalFormatSymbols(new Locale("pt", "BR")));
			final Integer NO_LIMITE = 95;
			StringBuilder detalhamento = null;
			
			if (totais.getTotalGasto().doubleValue() > totais.getLimite().doubleValue()) {
				
				diagnostico.setStatus(StatusDoOrc.ESTOUROU);
				
				diagnostico.setResumo("Estourou o teto");
				
				detalhamento = new StringBuilder();
				detalhamento.append("Gastou R$");
				
				detalhamento.append(df.format(totais.getTotalDisponivel()
						.multiply(new BigDecimal(-1))));
				
				detalhamento.append(" acima do teto de R$");
				detalhamento.append(df.format(totais.getLimite())).append(".");
				
			}else if (totais.getPercGasto() >= NO_LIMITE) {
				
				diagnostico.setStatus(StatusDoOrc.NO_LIMITE);
				
				diagnostico.setResumo("Ficou no limite");
				
				detalhamento = new StringBuilder();
				detalhamento.append("Usou ");
				detalhamento.append(percGasto).append("%");
				detalhamento.append(" do teto - sobrou pouco ");
				detalhamento.append("(R$");
				detalhamento.append(totais.getTotalDisponivel());
				detalhamento.append(".)");
				
			}else {
				
				diagnostico.setStatus(StatusDoOrc.SOBROU);
				
				diagnostico.setResumo("Dentro do teto");
				
				detalhamento = new StringBuilder();
				detalhamento.append("Usou ");
				detalhamento.append(percGasto).append("%");
				detalhamento.append(" do teto e ainda tem R$");
				detalhamento.append(df.format(totais.getTotalDisponivel())).append(".");
				
			}
			
			diagnostico.setDetalhamento(detalhamento.toString());
			
			resumo.setDiagnostico(diagnostico);
			
			if (totais.getDataDeInicio() != null) {
				
				resumo.setDataDeInicio(totais.getDataDeInicio());
				
				Long qtdeDeDias = 0L;
				
				if (totais.getDataDeTermino() != null) {
					
					qtdeDeDias = ChronoUnit.DAYS.between(totais.getDataDeInicio(), 
							totais.getDataDeTermino());									
					
				}else {

					if (totais.getMediaDeGastoDia().doubleValue() > 0) {

						LocalDate hoje = LocalDate.now();

						qtdeDeDias = ChronoUnit.DAYS.between(totais.getDataDeInicio(), hoje);

						Long qtdeDeDiasRestantes = totais.getLimite()
								.divide(totais.getMediaDeGastoDia(), 2, RoundingMode.HALF_EVEN)
								.longValue();

						LocalDate dataProjetada = hoje.plusDays(qtdeDeDiasRestantes);

						resumo.setDiasRestantes(qtdeDeDiasRestantes);

						resumo.setDataDeTerminoProjetada(dataProjetada);

					}

				}
				
				resumo.setDiasDecorridos(qtdeDeDias);

			}
			
			resumos.add(resumo);
			
		}
				
		List<GastoOrcPorCateg> gastosMaisRecentes = listarGastosDoOrcPor(
				resumos.get(resumos.size() - 1).getIdDoOrcamento());			

		MaiorGastoDoOrc maiorGasto = null;
		
		if (gastosMaisRecentes.size() > 0) {
			
			//Recupera da lista o maior gasta uma vez que ele vem 
			//ordenado por total geral
			GastoOrcPorCateg gasto = gastosMaisRecentes.get(0);

			maiorGasto = new MaiorGastoDoOrc();
			maiorGasto.setNomeDaCategoria(gasto.getNomeDaCategoria());
			maiorGasto.setValor(gasto.getTotalGasto());
			maiorGasto.setPercGasto(gasto.getPercGastoDoOrc());			

		}
		
		TotaisDaCompra totaisDaCompra = listaService.buscarTotaisDeComprasPor(ano, mes, idDaFamilia);
		
		EconomiaEmCompras economiaEmCompras = null;
		
		ResumoGeralDeCompras resumoGeralDeCompras = null;
		
		if (totaisDaCompra != null && totaisDaCompra.getQtdeDeItens() > 0) {

			economiaEmCompras = new EconomiaEmCompras();
			economiaEmCompras.setTotalEconomizado(totaisDaCompra.getTotalEconomizado());
			economiaEmCompras.setPercEconomizado(totaisDaCompra.getPercEconomizado());
			economiaEmCompras.setTotalEstimado(totaisDaCompra.getTotalEstimado());
			economiaEmCompras.setTotalComprado(totaisDaCompra.getTotalComprado());

			resumoGeralDeCompras = new ResumoGeralDeCompras();			
			
			resumoGeralDeCompras.setMediaDeCompra(totaisDaCompra
					.getMediaDeCompra().setScale(2, RoundingMode.HALF_EVEN));
			
			resumoGeralDeCompras.setQtdeDeCompras(totaisDaCompra.getQtdeDeCompras());
			resumoGeralDeCompras.setQtdeDeItens(totaisDaCompra.getQtdeDeItens());

		}		

		List<ResumoDeCompraDoProd> resumosDoProduto = listaService
				.listarResumosDeProdsPor(ano, mes, idDaFamilia, Pageable.ofSize(5));

		List<MaiorGastoDeProduto> maioresGastosDeProd =  new ArrayList<>();

		for (ResumoDeCompraDoProd resumo : resumosDoProduto) {
			maioresGastosDeProd.add(new MaiorGastoDeProduto(resumo));
		}

		IndicadoresGerais indicadores = new IndicadoresGerais();
		indicadores.setAno(ano);
		indicadores.setMes(mes);
		indicadores.setResumosGeraisDeOrc(resumos);
		indicadores.setGastosOrcPorCategs(gastosMaisRecentes);
		indicadores.setMaiorGastoDoOrc(maiorGasto);
		indicadores.setEconomiaEmCompras(economiaEmCompras);
		indicadores.setResumoGeralDeCompras(resumoGeralDeCompras);
		indicadores.setMaioresGastosDeProd(maioresGastosDeProd);

		return indicadores;

	}
	
	public List<GastoOrcPorCateg> listarGastosPor(
			@IdValido(nomeDoAtributo = "id do orçamento")
			Integer idDoOrcamento,
			@IdValido(nomeDoAtributo = "id da família")
			Integer idDaFamilia){
		
		Orcamento orcEncontrado = orcService.buscarPor(idDoOrcamento);
		
		Preconditions.checkArgument(orcEncontrado.getIdDaFamilia() == idDaFamilia,
				"Esse usuário não pode listar os gastos desse orçamento");
		
		return listarGastosDoOrcPor(idDoOrcamento);
		
	}
	
	private List<GastoOrcPorCateg> listarGastosDoOrcPor(
			@IdValido(nomeDoAtributo = "id do orçamento")
			Integer idDoOrcamento){
		
		List<GastoOrcPorCateg> gastosMaisRecentes = new ArrayList<>();
		
		List<GastoPorCategoria> gastosDasCategs = lanctoService
				.listarGastosDasCategsPor(idDoOrcamento);
		
		for (GastoPorCategoria gasto : gastosDasCategs) {
			gastosMaisRecentes.add(new GastoOrcPorCateg(gasto));
		}
		
		return gastosMaisRecentes;
		
	}

}
