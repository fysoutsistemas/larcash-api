package br.com.larcash.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.larcash.converter.MapConverter;
import br.com.larcash.dto.NovoPagto;
import br.com.larcash.entity.Assinatura;
import br.com.larcash.entity.PagtoDaAssinatura;
import br.com.larcash.entity.Usuario;
import br.com.larcash.repository.projection.MembroDaFamilia;
import br.com.larcash.service.AssinaturaService;
import br.com.larcash.service.FamiliaService;
import br.com.larcash.service.UsuarioService;
import br.com.larcash.util.TokenUtil;
import jakarta.transaction.Transactional;

@RestController
@RequestMapping("/familias")
public class FamiliaController {

	@Autowired
	private FamiliaService service;

	@Autowired
	private UsuarioService usuarioService;

	@Autowired
	private AssinaturaService assinaturaService;

	@Autowired
	private MapConverter converter;

	@Autowired
	private TokenUtil tokenUtil;

	@GetMapping("/membros")
	public ResponseEntity<?> listarMembros(
			@RequestHeader("Authorization") 
			String authHeader) {

		String loginDoToken = tokenUtil.extractLoginDo(authHeader);

		Usuario usuarioEncontrado = usuarioService.buscarPorLogin(loginDoToken);

		List<MembroDaFamilia> membros = service.listarMembrosPor(usuarioEncontrado);

		if (membros.isEmpty()) {
			return ResponseEntity.noContent().build();
		}

		return ResponseEntity.ok(converter.toJsonList(membros));

	}

	@GetMapping("/{idDaFamilia}/assinatura")
	public ResponseEntity<?> buscarPor(
			@PathVariable("idDaFamilia") 
			Integer idDaFamilia) {

		Assinatura assinaturaEncontrada = assinaturaService.buscarPor(idDaFamilia);

		return ResponseEntity.ok(converter.toJsonMap(assinaturaEncontrada));

	}
	
	@Transactional
	@PatchMapping("/{idDaFamilia}/assinatura/cortesia")
	public ResponseEntity<?> concederCortesiaPor(
			@PathVariable("idDaFamilia") 
			Integer idDaFamilia) {

		Assinatura assinaturaAtualizada = assinaturaService.concederCortesiaPor(idDaFamilia);

		return ResponseEntity.ok(converter.toJsonMap(assinaturaAtualizada));

	}
	
	@Transactional
	@PostMapping("/{idDaFamilia}/assinatura/pagtos")
	public ResponseEntity<?> renovarAcessoPor(
			@PathVariable("idDaFamilia")
			Integer idDaFamilia,
			@RequestBody 
			NovoPagto novoPagto) {
		
		Assinatura assinatura = assinaturaService.renovarAcessoPor(
				idDaFamilia, novoPagto.getValor());
		
		return ResponseEntity.ok(converter.toJsonMap(assinatura));
		
	}
	
	@GetMapping("/{idDaFamilia}/assinatura/pagtos")
	public ResponseEntity<?> listarPagtosPor(
			@PathVariable("idDaFamilia")
			Integer idDaFamilia){

		List<PagtoDaAssinatura> pagtos = assinaturaService.listarPagtosPor(idDaFamilia);

		if (pagtos.isEmpty()) {
			return ResponseEntity.noContent().build();
		}

		return ResponseEntity.ok(converter.toJsonList(pagtos));

	}

}
