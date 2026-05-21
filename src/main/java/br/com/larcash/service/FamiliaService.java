package br.com.larcash.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.google.common.base.Preconditions;

import br.com.larcash.entity.Familia;
import br.com.larcash.exception.RegistroNaoEncontradoException;
import br.com.larcash.repository.FamiliasRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Service
@Validated
public class FamiliaService {
	
	@Autowired
	private FamiliasRepository repository;
	
	public Familia inserir(
			@Valid
			@NotNull(message = "A nova família não pode ser nula")
			Familia novaFamilia) {
		
		Preconditions.checkArgument(novaFamilia.isNovo(), 
				"A nova família não deve possuir id");
		
		return repository.save(novaFamilia);

	}
	
	public Familia alterar(
			@Valid
			@NotNull(message = "A família salva não pode ser nula")
			Familia familiaSalva) {
		this.buscarPor(familiaSalva.getId());		
		return repository.save(familiaSalva);
	}

	public Familia buscarPorLogin(
			@NotBlank(message = "O login é obrigatório")
			String login) {
		
		Familia familiaEncontrada = repository.buscarPorLogin(login);
		
		Optional.ofNullable(familiaEncontrada)
				.orElseThrow(() -> new RegistroNaoEncontradoException(
						"Não existe família vinculada ao login informado"));
		
		return familiaEncontrada;
		
	}
	
	public Familia buscarPor(
			@NotNull(message = "O id é obrigatório")
			@Positive(message = "O id deve ser positivo")
			Integer id) {
		
		Familia familiaEncontrada = repository.buscarPor(id);
		
		Optional.ofNullable(familiaEncontrada)
				.orElseThrow(() -> new RegistroNaoEncontradoException(
						"Não existe família vinculada ao id informado"));
		
		return familiaEncontrada;
		
	}
	
}
