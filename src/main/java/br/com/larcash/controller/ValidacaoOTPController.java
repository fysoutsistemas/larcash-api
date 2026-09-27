package br.com.larcash.controller;

import java.util.Map;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.larcash.service.ValidacaoOTPService;

@RestController
@RequestMapping("/validacoes-otp")
public class ValidacaoOTPController {

	@Autowired
	private ValidacaoOTPService service;
	
	@PostMapping("/nova-conta")
	public ResponseEntity<?> gerarCodigoOtp(
			@RequestBody
			Map<String, Object> bodyMap){
		
		JSONObject bodyJSON = new JSONObject(bodyMap);

		String login = bodyJSON.optString("login");

		String telefone = bodyJSON.optString("telefone");

		this.service.gerarCodigoPor(login, telefone);

		return ResponseEntity.ok().build();

	}
	
	
}
