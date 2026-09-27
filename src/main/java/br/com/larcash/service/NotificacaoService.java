package br.com.larcash.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import br.com.larcash.integration.whatsout.WhatsoutClient;
import br.com.larcash.integration.whatsout.request.EnvioDeMsgRequest;
import br.com.larcash.integration.whatsout.request.LoginRequest;
import br.com.larcash.integration.whatsout.response.LoginResponse;
import jakarta.validation.constraints.NotBlank;

@Service
@Validated
public class NotificacaoService {
	
	@Autowired
	private WhatsoutClient client;
	
	@Value("${whatsout.login}")
	private String login;
	
	@Value("${whatsout.senha}")
	private String senha;
	
	@Value("${whatsout.nro-envio}")
	private String nroDeEnvio;
	
	public void enviarMsgPor(
			@NotBlank(message = "O número de destino é obrigatório")
			String nroDeDestino, 
			@NotBlank(message = "O conteúdo é obrigatório")
			String conteudo) {
		
		String token = gerarToken();
		
		EnvioDeMsgRequest request = new EnvioDeMsgRequest();
		request.setNumeroDeDestino(nroDeDestino.replaceFirst("\\+", ""));
		request.setConteudo(conteudo);

		this.client.realizar(token, nroDeEnvio, request);

	}
	
	private String gerarToken() {

		LoginRequest request = new LoginRequest();
		request.setLogin(login);
		request.setSenha(senha);
		
		LoginResponse resp = client.logar(request);
		
		return "Bearer " + resp.getToken();
		
	}
	
}
