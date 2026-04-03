package br.com.larcash.service;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.common.base.Preconditions;
import com.google.common.hash.Hashing;

import br.com.larcash.entity.Usuario;
import br.com.larcash.repository.UsuariosRepository;
import jakarta.validation.constraints.NotBlank;

@Service
public class AuthService {

	@Autowired
	private UsuariosRepository repository;
	
	@Value("${validade-em-horas}")
	private Integer validadeEmHoras;
	
	public String autenticar(
			@NotBlank(message = "O login é obrigatório")
			String login, 
			@NotBlank(message = "A senha é obrigatória")
			String senha) {
		
		Usuario usuarioEncontrado = repository.buscarPorLogin(login);
		
		String senhaCifrada = Hashing.sha256().hashString(senha, 
				StandardCharsets.UTF_8).toString();
		
		Preconditions.checkArgument(usuarioEncontrado != null && 
				usuarioEncontrado.getSenha().equals(senhaCifrada), 
				"Login ou senha inválidos");
		
		//Cria uma validade de 8 horas
		LocalDateTime validade = LocalDateTime.now().plusHours(validadeEmHoras);
		
		String baseDoToken = usuarioEncontrado.getLogin() + "," + validade.atZone(
				ZoneId.systemDefault()).toInstant().toEpochMilli();
		
		String tokenGerado = Base64.getEncoder().encodeToString(baseDoToken.getBytes());
		
		usuarioEncontrado.setUltimoToken(tokenGerado);
		
		this.repository.save(usuarioEncontrado);
		
		return tokenGerado;

	}
	
}
