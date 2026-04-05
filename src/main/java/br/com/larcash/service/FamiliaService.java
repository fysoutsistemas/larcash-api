package br.com.larcash.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.larcash.entity.Familia;
import br.com.larcash.exception.RegistroNaoEncontradoException;
import br.com.larcash.repository.FamiliasRepository;
import jakarta.validation.constraints.NotBlank;

@Service
public class FamiliaService {
	
	@Autowired
	private FamiliasRepository repository;

	public Familia buscarPorLogin(
			@NotBlank(message = "O login é obrigatório")
			String login) {
		
		Familia familiaEncontrada = repository.buscarPorLogin(login);
		
		Optional.ofNullable(familiaEncontrada)
			.orElseThrow(() -> new RegistroNaoEncontradoException(
					"Não existe família vinculada ao login informado"));
		
		return familiaEncontrada;
		
	}
	
}
