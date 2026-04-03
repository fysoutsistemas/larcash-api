package br.com.larcash.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.larcash.entity.Usuario;
import br.com.larcash.exception.RegistroNaoEncontradoException;
import br.com.larcash.repository.UsuariosRepository;
import jakarta.validation.constraints.NotBlank;

@Service
public class UsuarioService {
	
	@Autowired
	private UsuariosRepository repository;
	
	public Usuario buscarPorLogin(
			@NotBlank(message = "O login é obrigatório")
			String login) {
		
		Usuario usuarioEncontrado = repository.buscarPorLogin(login); 
		
		Optional.ofNullable(usuarioEncontrado)
			.orElseThrow(() -> new RegistroNaoEncontradoException(
        			"Não existe usuário vinculado ao login '" + login + "'"));
		
		return usuarioEncontrado;
	}

}
