package br.com.larcash.integration.whatsout;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import br.com.larcash.integration.whatsout.request.EnvioDeMsgRequest;
import br.com.larcash.integration.whatsout.request.LoginRequest;
import br.com.larcash.integration.whatsout.response.ChatbotResponse;
import br.com.larcash.integration.whatsout.response.LoginResponse;

@FeignClient(name = "whatsout-client", url = "${whatsout.url}", path = "/api")
public interface WhatsoutClient {
	
	@PostMapping("/auth")
	public LoginResponse logar(
			@RequestBody
			LoginRequest request);
	
	@GetMapping("/chatbot/{nroDoCelular}")
	public ChatbotResponse buscarPor(
			@RequestHeader("Authorization") 
			String token,
			@PathVariable("nroDoCelular")
			String nroDoCelular);
	
	@PostMapping("/chatbot/{nroDoCelular}/envio")
	public void realizar(
			@RequestHeader("Authorization") 
			String token,
			@PathVariable("nroDoCelular")
			String nroDoCelular,
			@RequestBody
			EnvioDeMsgRequest envio);

}
