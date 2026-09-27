package br.com.larcash.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.larcash.converter.MapConverter;
import br.com.larcash.entity.Usuario;
import br.com.larcash.repository.projection.MembroDaFamilia;
import br.com.larcash.service.FamiliaService;
import br.com.larcash.service.UsuarioService;
import br.com.larcash.util.TokenUtil;

@RestController
@RequestMapping("/familias")
public class FamiliaController {

	@Autowired
	private FamiliaService service;
	
	@Autowired
	private UsuarioService usuarioService;
	
	@Autowired
	private MapConverter converter;
	
	@Autowired
	private TokenUtil tokenUtil;
	
	@GetMapping("/membros")
	public ResponseEntity<?> listarMembros(
			@RequestHeader("Authorization")
			String authHeader){

		String loginDoToken = tokenUtil.extractLoginDo(authHeader);

		Usuario usuarioEncontrado = usuarioService.buscarPorLogin(loginDoToken);

		List<MembroDaFamilia> membros = service.listarMembrosPor(usuarioEncontrado);

		if (membros.isEmpty()) {
			return ResponseEntity.noContent().build();
		}

		return ResponseEntity.ok(converter.toJsonList(membros));

	}
	
}
