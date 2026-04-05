package br.com.larcash.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.larcash.entity.Orcamento;
import br.com.larcash.exception.RegistroNaoEncontradoException;
import br.com.larcash.repository.OrcamentosRepository;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Service
public class OrcamentoService {

	@Autowired
	private OrcamentosRepository repository;
	
	public Orcamento buscarPor(
			@NotBlank(message = "O login é obrigatório")
			String login, 
			@NotNull(message = "O ano é obrigatório")
			@Positive(message = "O ano deve ser positivo")
			Integer ano, 
			@NotNull(message = "O mês é obrigatório")
			@Min(value = 1, message = "O mês deve estar entre 1 e 12")
			@Max(value = 12, message = "O mês deve estar entre 1 e 12")
			Integer mes) {
		
		Orcamento orcamentoEncontrado = repository.buscarPor(ano, mes, login); 
		
		Optional.ofNullable(orcamentoEncontrado)
			.orElseThrow(() -> new RegistroNaoEncontradoException(
    			"Não existe orçamento vinculado aos parâmetros informados"));
		
		return orcamentoEncontrado;
		
	}
	
}
