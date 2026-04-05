package br.com.larcash.controller;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.larcash.converter.MapConverter;
import br.com.larcash.dto.PainelFinanceiro;
import br.com.larcash.entity.Lancamento;
import br.com.larcash.entity.Usuario;
import br.com.larcash.service.LanctoService;
import br.com.larcash.service.UsuarioService;
import br.com.larcash.util.TokenUtil;
import jakarta.transaction.Transactional;

@RestController
@RequestMapping("/lanctos")
public class LanctoController {

	@Autowired
	private LanctoService service;
	
	@Autowired
	private UsuarioService usuarioService;
	
	@Autowired
	private MapConverter converter;
	
	@Autowired
	private TokenUtil tokenUtil;
	
	@PostMapping
	@Transactional
	public ResponseEntity<?> inserir(
			@RequestHeader("Authorization") 
			String authHeader,
			@RequestBody
			Lancamento novoLancto){
		
		String loginDoToken = tokenUtil.extractLoginDo(authHeader);
		
		Usuario usuario = usuarioService.buscarPorLogin(loginDoToken);
		
		novoLancto.setUsuario(usuario);
		
		novoLancto.setFamilia(usuario.getFamilia());
		
		Lancamento lanctoSalvo = service.inserir(novoLancto);
		
		return ResponseEntity.created(URI.create("/lanctos/id/" 
				+ lanctoSalvo.getId())).build();
		
	}
	
	@GetMapping("/id/{id}")
	public ResponseEntity<?> buscarPor(
			@RequestHeader("Authorization") 
			String authHeader,
			@PathVariable("id")
			Integer id){

		String loginDoToken = tokenUtil.extractLoginDo(authHeader);
		
		Lancamento lanctoEncontrado = service.buscarPor(loginDoToken, id);

		return ResponseEntity.ok(converter.toJsonMap(lanctoEncontrado));

	}
		
	@Transactional
	@DeleteMapping("/id/{id}")
	public ResponseEntity<?> removerPor(
			@RequestHeader("Authorization") 
			String authHeader,
			@PathVariable("id")
			Integer id){
		
		String loginDoToken = tokenUtil.extractLoginDo(authHeader);
		
		Lancamento lanctoRemovido = service.removerPor(loginDoToken, id);
		
		return ResponseEntity.ok(converter.toJsonMap(lanctoRemovido));
		
	}
	
	@GetMapping("/painel/ano/{ano}/mes/{mes}")
	public ResponseEntity<?> buscarPainelPor(
			@RequestHeader("Authorization") 
			String authHeader,
			@PathVariable("ano")
			Integer ano, 
			@PathVariable("mes")
			Integer mes){
		
		String loginDoToken = tokenUtil.extractLoginDo(authHeader);
		
		PainelFinanceiro painel = service.buscarPainelPor(loginDoToken, ano, mes);
		
		return ResponseEntity.ok(converter.toJsonMap(painel, "usuario"));
		
	}
	
}
