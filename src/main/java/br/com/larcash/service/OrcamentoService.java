package br.com.larcash.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.larcash.entity.Orcamento;
import br.com.larcash.entity.Usuario;
import br.com.larcash.exception.RegistroNaoEncontradoException;
import br.com.larcash.repository.OrcamentosRepository;
import jakarta.validation.constraints.NotBlank;

@Service
public class OrcamentoService {

	@Autowired
	private UsuarioService usuarioService;
	
	@Autowired
	private OrcamentosRepository repository;	
	
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
	
}
