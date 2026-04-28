package br.com.larcash.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.google.common.base.Preconditions;

import br.com.larcash.dto.ProgressoDoOrcamento;
import br.com.larcash.entity.Orcamento;
import br.com.larcash.entity.Usuario;
import br.com.larcash.enums.Status;
import br.com.larcash.exception.RegistroNaoEncontradoException;
import br.com.larcash.repository.OrcamentosRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Service
@Validated
public class OrcamentoService {

	@Autowired
	private UsuarioService usuarioService;
	
	@Autowired
	private FamiliaService familiaService;
	
	@Lazy
	@Autowired
	private LanctoService lanctoService;
	
	@Autowired
	private OrcamentosRepository repository;
	
	public Orcamento inserir(
			@Valid
			@NotNull(message = "O novo orçamento não deve ser nulo")
			Orcamento novoOrcamento) {

		Preconditions.checkArgument(novoOrcamento.isNovo(), 
				"O novo orçamento não deve possuir id");		
		
		this.familiaService.buscarPor(novoOrcamento.getIdDaFamilia());
		
		this.repository.inativarTodosPor(novoOrcamento.getIdDaFamilia());
		
		novoOrcamento.setStatus(Status.A);
		
		Orcamento orcamentoSalvo = repository.save(novoOrcamento);

		return buscarPor(orcamentoSalvo.getId());

	}	
	
	public Orcamento alterarLimitePor(
			@NotBlank(message = "O login é obrigatório")
			String login, 
			@NotNull(message = "O limite é obrigatório")
			@Positive(message = "O novo limite deve ser positivo")
			BigDecimal limite) {

		Orcamento orcamentoEncontrado = buscarUltimoPor(login);

		orcamentoEncontrado.setLimite(limite);

		this.repository.save(orcamentoEncontrado);

		return buscarPor(orcamentoEncontrado.getId());

	}
	
	public ProgressoDoOrcamento buscarProgressoPor(
			@NotBlank(message = "O login é obrigatório")
			String login) {

		Orcamento orcamentoEncontrado = buscarUltimoPor(login);

		BigDecimal totalGasto = lanctoService.somarTotalGastoPor(
				orcamentoEncontrado.getId());

		Integer percentualGasto = totalGasto.divide(orcamentoEncontrado.getLimite(), 
				2, RoundingMode.HALF_EVEN).multiply(new BigDecimal(100)).intValue();
		
		BigDecimal totalRestante = orcamentoEncontrado.getLimite().subtract(totalGasto);

		return new ProgressoDoOrcamento(orcamentoEncontrado.getLimite(), 
				totalGasto, percentualGasto, totalRestante);

	}
	
	public Orcamento buscarUltimoPor(
			@NotBlank(message = "O login é obrigatório")
			String login) {
				
		Usuario usuarioEncontrado = usuarioService.buscarPorLogin(login);
		
		Orcamento orcamentoEncontrado = repository.buscarUltimoPor(
				usuarioEncontrado.getIdDaFamilia());
		
		Optional.ofNullable(orcamentoEncontrado)
				.orElseThrow(() -> new RegistroNaoEncontradoException(
						"Não existe orçamento vinculado aos parâmetros informados"));
	
		return orcamentoEncontrado;

	}
	
	public Orcamento buscarPor(Integer id) {

		Orcamento orcamentoEncontrado = repository.buscarPor(id);

		Optional.ofNullable(orcamentoEncontrado)
				.orElseThrow(() -> new RegistroNaoEncontradoException(
						"Não existe orçamento vinculado ao id informado"));

		return orcamentoEncontrado;

	}
	
}
