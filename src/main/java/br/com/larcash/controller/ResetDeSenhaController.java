package br.com.larcash.controller;

import java.util.Map;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.larcash.converter.MapConverter;
import br.com.larcash.dto.SenhaResetada;
import br.com.larcash.entity.ResetDeSenha;
import br.com.larcash.service.ResetDeSenhaService;

@RestController
@RequestMapping("/reset-senha")
public class ResetDeSenhaController {
	
	@Autowired
	private ResetDeSenhaService service;
	
	@Autowired
	private MapConverter converter;

	@PostMapping("/{login}")
	public ResponseEntity<?> gerarCodigoOTP(
			@PathVariable("login")
			String login){		

		this.service.gerarCodigoPor(login);

		return ResponseEntity.ok().build();
		
	}

	@PostMapping("/verificacao")
	public ResponseEntity<?> validarCodigoPor(
			@RequestBody
			Map<String, Object> bodyMap){
		
		JSONObject bodyJSON = new JSONObject(bodyMap);

		String login = bodyJSON.optString("login");

		String telefone = bodyJSON.optString("codigo");

		ResetDeSenha reset = service.validarCodigoPor(login, telefone);		
		
		return ResponseEntity.ok(converter.toJsonMap(reset));

	}
	
	@PatchMapping
	public ResponseEntity<?> processar(
			@RequestBody
			SenhaResetada senhaResetada){
		
		this.service.processar(senhaResetada);
		
		return ResponseEntity.ok().build();
		
	}
	
}
