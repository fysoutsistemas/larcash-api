package br.com.larcash.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.base.Preconditions;

import br.com.larcash.dto.PainelFinanceiro;
import br.com.larcash.dto.ResumoGeral;
import br.com.larcash.dto.ResumoPorCategoria;
import br.com.larcash.entity.Categoria;
import br.com.larcash.entity.Lancamento;
import br.com.larcash.entity.Orcamento;
import br.com.larcash.entity.Usuario;
import br.com.larcash.exception.RegistroNaoEncontradoException;
import br.com.larcash.repository.LanctosRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Service
public class LanctoService {
	
	@PersistenceContext
	private EntityManager em;

	@Autowired
	private LanctosRepository repository;
	
	@Autowired
	private UsuarioService usuarioService;
	
	@Autowired
	private OrcamentoService orcamentoService;
	
	public Lancamento inserir(
			@Valid
			@NotNull(message = "O novo lancamento não pode ser nulo")
			Lancamento novoLancto) {
		
		Lancamento lanctoSalvo = repository.save(novoLancto);
		
		this.em.detach(lanctoSalvo);
		
		return repository.buscarPor(lanctoSalvo.getId(), lanctoSalvo.getLogin());
		
	}
	
	public Lancamento buscarPor(String login, Integer id) {
		
		Lancamento lanctoEncontrado = repository.buscarPor(id, login);
		
		Optional.ofNullable(lanctoEncontrado)
			.orElseThrow(() -> new RegistroNaoEncontradoException(
    			"Não existe lançamento vinculado ao id '" + id + "'"));
		
		return lanctoEncontrado;
		
	}
	
	public Lancamento removerPor(
			@NotBlank(message = "O login é obrigatório")
			String login,
			@NotNull(message = "O id é obrigatório")
			@Positive(message = "O id deve ser positivo")
			Integer id) {
		
		Lancamento lanctoDaRemocao = buscarPor(login, id);

		this.repository.removerPor(login, id);

		return lanctoDaRemocao;

	}
	
	public PainelFinanceiro buscarPainelPor(
			@NotBlank(message = "O login é obrigatório")
			String login, 
			@NotNull(message = "O ano é obrigatório")
			@Positive(message = "O ano deve ser positivo")
			Integer ano, 
			@NotNull(message = "O mês é obrigatório")
			@Min(value = 1, message = "O mês deve estar entre 1 e 12")
			@Max(value = 12, message = "O mês deve estar entre 1 e 12")
			Integer mes) {
		
		Usuario usuarioEncontrado = usuarioService.buscarPorLogin(login);
		
		Preconditions.checkArgument(usuarioEncontrado != null, "O login não existe");
		
		Orcamento orcamentoEncontrado = orcamentoService.buscarPor(login, ano, mes);
		
		List<Lancamento> lancamentos = repository.listarPor(ano, mes, 
				usuarioEncontrado.getIdDaFamilia());			
		
		List<Categoria> categorias = extractCategoriasFrom(lancamentos);
		
		List<ResumoPorCategoria> resumosPorCategoria = new ArrayList<>();
		
		ResumoGeral resumoGeral = new ResumoGeral();
		
		for (Categoria categ : categorias) {
			
			ResumoPorCategoria resumoPorCategoria = new ResumoPorCategoria(categ);
			
			for (Lancamento lancto : lancamentos) {
				
				if (lancto.getCategoria().equals(categ)) {
					BigDecimal gastosSomados = lancto.getValor().add(resumoPorCategoria.getGastos());
					resumoPorCategoria.setGastos(gastosSomados);
				}
				
			}
			
			Integer percentual = 100;
			
			if (categ.getLimite().doubleValue() > 0.0) {				
				percentual = resumoPorCategoria.getGastos()
						.divide(categ.getLimite(), 2, RoundingMode.HALF_EVEN)
						.multiply(new BigDecimal(100))
						.intValue();
			}
			
			resumoPorCategoria.setPercentual(percentual);
			
			resumosPorCategoria.add(resumoPorCategoria);
			
			BigDecimal totalGeralGasto = resumoGeral.getTotalGasto().add(resumoPorCategoria.getGastos());
			
			resumoGeral.setTotalGasto(totalGeralGasto);
			
		}
		
		Integer percentual = 100;
		
		if (orcamentoEncontrado.getLimite().doubleValue() > 0.0) {
			percentual = resumoGeral.getTotalGasto()
					.divide(orcamentoEncontrado.getLimite(), 2, RoundingMode.HALF_EVEN)
					.multiply(new BigDecimal(100))
					.intValue();			
		}
		
		resumoGeral.setPercentualGasto(percentual);
		resumoGeral.setTotalOrcado(orcamentoEncontrado.getLimite());
		resumoGeral.setResumosPorCategoria(resumosPorCategoria);
		
		PainelFinanceiro painel = new PainelFinanceiro(ano, mes, login);
		painel.setResumoGeral(resumoGeral);
		painel.setLancamentos(lancamentos);
		
		return painel;
		
	}
	
	private List<Categoria> extractCategoriasFrom(List<Lancamento> lancamentos){
		
		List<Categoria> categorias = new ArrayList<>();
		
		for (Lancamento lancto : lancamentos) {
			
			if (!categorias.contains(lancto.getCategoria())) {
				categorias.add(lancto.getCategoria());
			}
			
		}
		
		return categorias;
		
	}
	
}
