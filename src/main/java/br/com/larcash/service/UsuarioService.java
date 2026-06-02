package br.com.larcash.service;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.hash.Hashing;

import br.com.larcash.entity.Usuario;
import br.com.larcash.exception.RegistroNaoEncontradoException;
import br.com.larcash.repository.UsuariosRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Service
@Validated
public class UsuarioService {
	
	@Autowired
	private UsuariosRepository repository;
	
	@Autowired
	private FamiliaService familiaService;
	
	public Usuario inserir(
			@Valid
			@NotNull(message = "O novo usuário não pode ser nulo")
			Usuario novoUsuario) {

		String senhaCifrada = Hashing.sha256().hashString(novoUsuario
				.getSenha(), StandardCharsets.UTF_8).toString();

		Usuario outroUsuario = repository.buscarPorLogin(novoUsuario.getLogin());

		//Impede que o login seja utilizado caso existe 
		//outro usuário utilizando antes
		Preconditions.checkArgument(outroUsuario == null, 
				"Já existe um usuário com o login informado");

		//Lança exceção se não encontrar familia vinculada ao id
		//impedindo assim ids inexistentes no objeto do usuário
		this.familiaService.buscarPor(novoUsuario.getIdDaFamilia());
		
		novoUsuario.setSenha(senhaCifrada);		

		return repository.save(novoUsuario);

	}
	
	public Usuario atualizarPor(
			@NotBlank(message = "O login é obrigatório")
			String login,
			@NotBlank(message = "O nome completo é obrigatório")
			String nomeCompleto,
			String senhaAtual, 
			String novaSenha) {
		
		Usuario usuarioEncontrado = buscarPorLogin(login);
		
		if (!Strings.isNullOrEmpty(senhaAtual)) {
			
			Preconditions.checkArgument(!Strings.isNullOrEmpty(novaSenha), 
					"A nova senha é obrigatória");

			String senhaCifrada = Hashing.sha256().hashString(senhaAtual, 
					StandardCharsets.UTF_8).toString();
			
			Preconditions.checkArgument(usuarioEncontrado.getSenha()
					.equals(senhaCifrada), "A senha atual é inválida");
			
			String novaSenhaCifrada = Hashing.sha256().hashString(novaSenha, 
					StandardCharsets.UTF_8).toString();
			
			usuarioEncontrado.setSenha(novaSenhaCifrada);
			
		}

		usuarioEncontrado.setNomeCompleto(nomeCompleto);

		return this.repository.save(usuarioEncontrado);

	}
	
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
