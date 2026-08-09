package br.com.larcash.service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.google.common.base.Preconditions;

import br.com.larcash.dto.NovoMembro;
import br.com.larcash.entity.Convite;
import br.com.larcash.entity.Usuario;
import br.com.larcash.enums.StatusDoConvite;
import br.com.larcash.exception.RegistroNaoEncontradoException;
import br.com.larcash.repository.ConvitesRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Service
@Validated
public class ConviteService {
	
	@PersistenceContext
	private EntityManager em;

	@Autowired
	private ConvitesRepository repository;
	
	@Autowired
	private UsuarioService usuarioService;
	
	@Value("${validade-em-horas}")
	private Integer validadeEmHoras;
	
	@Value("${url-view}")
	private String urlDaView;
	
	@Transactional
	public Convite criarConvitePor(
			@NotBlank(message = "O login do chefe é obrigatório")
			String loginDoChefe) {
		
		Usuario usuarioEncontrado = usuarioService.buscarPorLogin(loginDoChefe);
		
		Preconditions.checkArgument(usuarioEncontrado.isChefeDeFamilia(), 
				"O convite só pode ser realizado por um chefe de família");
		
		//Cria uma validade de 8 horas
		LocalDateTime validade = LocalDateTime.now().plusHours(validadeEmHoras);
		
		//<login>,<nomeCompleto>,<idDaFamilia>,<nomeDaFamilia>,<ValidadeInMillis>
		String baseDoToken = usuarioEncontrado.getLogin() + "," 
				+ usuarioEncontrado.getNomeCompleto() + ","
				+ usuarioEncontrado.getIdDaFamilia() + ","
				+ usuarioEncontrado.getFamilia().getNome() + ","
				+ validade.atZone(ZoneId.systemDefault())
						.toInstant().toEpochMilli();
		
		String tokenDoConvite = Base64.getEncoder().encodeToString(baseDoToken.getBytes());
		
		Convite novoConvite = new Convite();
		novoConvite.setChefe(usuarioEncontrado);
		novoConvite.setFamilia(usuarioEncontrado.getFamilia());
		novoConvite.setToken(tokenDoConvite);
		novoConvite.setLink(urlDaView + "/" + tokenDoConvite);
		novoConvite.setValidoAte(validade);
		
		Convite conviteSalvo = repository.save(novoConvite);
		
		this.em.detach(conviteSalvo);
		
		return buscarPor(tokenDoConvite);
		
	}
	
	@Transactional
	public void criar(
			@Valid
			@NotNull(message = "O novo membro não pode ser nulo")
			NovoMembro novoMembro) {
		
		String tokenDoConvite = novoMembro.getTokenDoConvite();
		
		//[0]<login>,[1]<nomeCompleto>,[2]<idDaFamilia>,[3]<nomeDaFamilia>,[4]<ValidadeInMillis>
		String dadosDoToken[] = new String(Base64.getDecoder()
	    		.decode(tokenDoConvite.getBytes())).split(",");
		
		Preconditions.checkArgument(dadosDoToken.length == 5, "Token de convite inválido");
		
		Long validadeInMillis = Long.valueOf(dadosDoToken[4]);
	    
	    Instant instant = Instant.ofEpochMilli(validadeInMillis);
	    
	    LocalDateTime validade = LocalDateTime.ofInstant(instant, 
	    		ZoneId.systemDefault());
	    
	    Preconditions.checkArgument(validade.isAfter(LocalDateTime.now()), 
	    		"Token de convite fora do prazo de validade");
	    
	    Convite conviteEncontrado = buscarPor(tokenDoConvite);
	    
	    Preconditions.checkArgument(!conviteEncontrado.isConfirmado(), 
	    		"O token do convite já foi utilizado");

	    Usuario novoUsuario = new Usuario();
	    novoUsuario.setLogin(novoMembro.getLogin());
	    novoUsuario.setSenha(novoMembro.getSenha());
	    novoUsuario.setNomeCompleto(novoMembro.getNomeCompleto());
	    novoUsuario.setFamilia(conviteEncontrado.getFamilia());
	    novoUsuario.setTelefone(novoMembro.getTelefone());

	    this.usuarioService.inserir(novoUsuario);

	    conviteEncontrado.setStatus(StatusDoConvite.CONFIRMADO);

	    this.repository.save(conviteEncontrado);

	}
	
	public Convite buscarPor(String tokenDoConvite) {

		Convite conviteEncontrado = repository.buscarPor(tokenDoConvite);

		Optional.ofNullable(conviteEncontrado)
				.orElseThrow(() -> new RegistroNaoEncontradoException(
						"Não existe convite vinculado ao token informado"));

		return conviteEncontrado;

	}
	
}
