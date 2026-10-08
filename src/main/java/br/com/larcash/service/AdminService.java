package br.com.larcash.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import br.com.larcash.entity.Administrador;
import br.com.larcash.exception.RegistroNaoEncontradoException;
import br.com.larcash.repository.AdminsRepository;
import jakarta.validation.constraints.NotBlank;

@Service
@Validated
public class AdminService {
	
	@Autowired
	private AdminsRepository repository;
	
	public Administrador buscarPorLogin(
			@NotBlank(message = "O login é obrigatório")
			String login) {

		Administrador adminEncontrado = repository.buscarPor(login);

		Optional.ofNullable(adminEncontrado)
			.orElseThrow(() -> new RegistroNaoEncontradoException(
    			"Não existe admin vinculado ao login '" + login + "'"));

		return adminEncontrado;

	}

}
