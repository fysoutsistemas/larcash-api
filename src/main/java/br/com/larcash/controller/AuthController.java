package br.com.larcash.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.larcash.dto.SolicitacaoDeToken;
import br.com.larcash.service.AuthService;
import jakarta.transaction.Transactional;

@RestController
@RequestMapping("/auth")
public class AuthController {
	
	@Autowired
	private AuthService service;

	@Transactional
	@PostMapping	
	public ResponseEntity<?> logar(
			@RequestBody 
			SolicitacaoDeToken solicitacao){		
		
		String tokenGerado = service.autenticar(solicitacao
				.getLogin(), solicitacao.getSenha());
		
		Map<String, Object> response = new HashMap<String, Object>();
		
		response.put("token", tokenGerado);
		
		return ResponseEntity.ok(response);

	}
	
}
