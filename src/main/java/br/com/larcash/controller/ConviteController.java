package br.com.larcash.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.larcash.dto.NovoMembro;
import br.com.larcash.entity.Convite;
import br.com.larcash.service.ConviteService;
import br.com.larcash.util.TokenUtil;

@RestController
@RequestMapping("/convites")
public class ConviteController {
	
	@Autowired
	private ConviteService service;
	
	@Autowired
	private TokenUtil tokenUtil;
	
	@PostMapping
	public ResponseEntity<?> gerar(
			@RequestHeader("Authorization")
			String authHeader){
		
		String loginDoToken = tokenUtil.extractLoginDo(authHeader);
		
		Convite convite = service.criarConvitePor(loginDoToken);
		
		Map<String, Object> response = new HashMap<String, Object>();
		
		response.put("link-novo-membro", convite.getLink());
		
		return ResponseEntity.ok(response);

	}
	
	@PostMapping("/registrar")
	public ResponseEntity<?> registrar(
			@RequestBody
			NovoMembro novoMembro){
		this.service.criar(novoMembro);
		return ResponseEntity.ok().build();
	}
	
}
