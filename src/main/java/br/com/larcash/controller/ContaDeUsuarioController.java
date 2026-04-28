package br.com.larcash.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.larcash.converter.MapConverter;
import br.com.larcash.dto.ContaDeUsuarioEditada;
import br.com.larcash.dto.NovaContaDeUsuario;
import br.com.larcash.dto.ResumoDaContaDeUsuario;
import br.com.larcash.service.ContaDeUsuarioService;
import br.com.larcash.util.TokenUtil;

@RestController
@RequestMapping("/contas-usuarios")
public class ContaDeUsuarioController {
	
	@Autowired
	private ContaDeUsuarioService service;
	
	@Autowired
	private MapConverter converter;
	
	@Autowired
	private TokenUtil tokenUtil;
		
	@PostMapping("/registrar")
	public ResponseEntity<?> registrar(
			@RequestBody
			NovaContaDeUsuario novaConta) {
		this.service.criar(novaConta);		
		return ResponseEntity.ok().build();
	}
	
	@PutMapping("/me")
	public ResponseEntity<?> atualizar(
			@RequestHeader("Authorization")
			String authHeader,
			@RequestBody
			ContaDeUsuarioEditada contaEditada){
		String loginDoToken = tokenUtil.extractLoginDo(authHeader);
		contaEditada.setLogin(loginDoToken);
		ResumoDaContaDeUsuario resumoAtualizado = service.atualizar(contaEditada);
		return ResponseEntity.ok(converter.toJsonMap(resumoAtualizado));
	}
	
	@GetMapping("/me")
	public ResponseEntity<?> buscarResumoPor(
			@RequestHeader("Authorization")
			String authHeader){
		
		String loginDoToken = tokenUtil.extractLoginDo(authHeader);
		
		ResumoDaContaDeUsuario resumoEncontrado = service.buscarResumoPor(loginDoToken);
		
		return ResponseEntity.ok(converter.toJsonMap(resumoEncontrado));
		
	}

}
