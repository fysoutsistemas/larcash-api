package br.com.larcash.service;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.google.common.base.Preconditions;
import com.google.common.hash.Hashing;

import br.com.larcash.entity.Administrador;
import br.com.larcash.entity.Assinatura;
import br.com.larcash.entity.Usuario;
import br.com.larcash.repository.AdminsRepository;
import br.com.larcash.repository.UsuariosRepository;
import jakarta.validation.constraints.NotBlank;

@Service
@Validated
public class AuthService {

	@Autowired
	private UsuariosRepository usuariosRepository;	
	
	@Autowired
	private AdminsRepository adminsRepository; 
	
	@Autowired
	private AssinaturaService assinaturaService;
	
	@Value("${validade-em-horas}")
	private Integer validadeEmHoras;
	
	public String autenticarAdmin(
			@NotBlank(message = "O login é obrigatório")
			String login, 
			@NotBlank(message = "A senha é obrigatória")
			String senha) {

		Administrador adminEncontrado = adminsRepository.buscarPor(login);

		String senhaCifrada = Hashing.sha256().hashString(senha, 
				StandardCharsets.UTF_8).toString();

		Preconditions.checkArgument(adminEncontrado != null && 
				adminEncontrado.getSenha().equals(senhaCifrada), 
				"Login ou senha inválidos");

		//Cria uma validade de 8 horas
		LocalDateTime validade = LocalDateTime.now().plusHours(validadeEmHoras);

		String baseDoToken = adminEncontrado.getLogin() 
				+ "," + validade.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

		String tokenGerado = Base64.getEncoder().encodeToString(baseDoToken.getBytes());

		adminEncontrado.setUltimoToken(tokenGerado);

		this.adminsRepository.save(adminEncontrado);

		return tokenGerado;

	}
	
	public String autenticar(
			@NotBlank(message = "O login é obrigatório")
			String login, 
			@NotBlank(message = "A senha é obrigatória")
			String senha) {

		Usuario usuarioEncontrado = usuariosRepository.buscarPorLogin(login);
		
		String senhaCifrada = Hashing.sha256().hashString(senha, 
				StandardCharsets.UTF_8).toString();
		
		Preconditions.checkArgument(usuarioEncontrado != null && 
				usuarioEncontrado.getSenha().equals(senhaCifrada), 
				"Login ou senha inválidos");
		
		Integer idDaFamilia = usuarioEncontrado.getIdDaFamilia();
		
		Assinatura assinaturaEncontrada = assinaturaService.buscarPor(idDaFamilia);
		
		//Cria uma validade de 8 horas
		LocalDateTime validade = LocalDateTime.now().plusHours(validadeEmHoras);
		
		Long validadeInMillis = validade.atZone(ZoneId
				.systemDefault()).toInstant().toEpochMilli();
		
		//Cria a validade de acordo com o que foi definido na assinatura
		LocalDateTime acessoAte = assinaturaEncontrada.getAcessoAte();
		
		Long validadeDaAssinatura = acessoAte.atZone(ZoneId
				.systemDefault()).toInstant().toEpochMilli(); 
		
		String baseDoToken = usuarioEncontrado.getLogin() + "," + validadeInMillis 
				+ "," + idDaFamilia + "," + validadeDaAssinatura;
		
		String tokenGerado = Base64.getEncoder().encodeToString(baseDoToken.getBytes());
		
		usuarioEncontrado.setUltimoToken(tokenGerado);
		
		usuarioEncontrado.setDataDoUltimoLogin(LocalDateTime.now());
		
		this.usuariosRepository.save(usuarioEncontrado);
		
		return tokenGerado;

	}
	
}
