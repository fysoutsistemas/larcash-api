package br.com.larcash.controller;

import java.math.BigDecimal;
import java.util.Map;

import org.json.JSONObject;
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
import br.com.larcash.dto.ProgressoDoOrcamento;
import br.com.larcash.entity.Orcamento;
import br.com.larcash.entity.Usuario;
import br.com.larcash.service.OrcamentoService;
import br.com.larcash.service.UsuarioService;
import br.com.larcash.util.TokenUtil;
import jakarta.transaction.Transactional;

@RestController
@RequestMapping("/orcamentos")
public class OrcamentoController {
	
	@Autowired
	private OrcamentoService service;
	
	@Autowired
	private UsuarioService usuarioService;
	
	@Autowired
	private MapConverter converter;
	
	@Autowired
	private TokenUtil tokenUtil;
	
	@PostMapping("/me")
	@Transactional
	public ResponseEntity<?> inserir(
			@RequestHeader("Authorization")
			String authHeader, 
			@RequestBody
			Orcamento novoOrcamento){

		String loginDoToken = tokenUtil.extractLoginDo(authHeader);

		Usuario usuarioEncontrado = usuarioService.buscarPorLogin(loginDoToken);

		novoOrcamento.setFamilia(usuarioEncontrado.getFamilia());

		this.service.inserir(novoOrcamento);

		return ResponseEntity.ok().build();

	}
	
	@PutMapping("/me")
	@Transactional
	public ResponseEntity<?> alterar(
			@RequestHeader("Authorization")
			String authHeader, 
			@RequestBody
			Map<String, Object> bodyMap){
		
		String loginDoToken = tokenUtil.extractLoginDo(authHeader);
		
		JSONObject bodyJson = new JSONObject(bodyMap);

		Usuario usuarioEncontrado = usuarioService.buscarPorLogin(loginDoToken);
		
		BigDecimal limite = bodyJson.optBigDecimal("limite", new BigDecimal(0.0));		
		
		Orcamento orcamentoAlterado = service.alterarLimitePor(
				usuarioEncontrado.getLogin(), limite);

		return ResponseEntity.ok(converter.toJsonMap(orcamentoAlterado));

	}
	
	@GetMapping("/progresso/me")
	public ResponseEntity<?> buscarProgressoPor(
			@RequestHeader("Authorization")
			String authHeader){
		
		String loginDoToken = tokenUtil.extractLoginDo(authHeader);
		
		ProgressoDoOrcamento progresso = service.buscarProgressoPor(loginDoToken);
		
		return ResponseEntity.ok(converter.toJsonMap(progresso));

	}

}
