package br.com.larcash.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.larcash.converter.MapConverter;
import br.com.larcash.dto.NovoLimiteDaCategoria;
import br.com.larcash.entity.Categoria;
import br.com.larcash.entity.Orcamento;
import br.com.larcash.enums.Status;
import br.com.larcash.service.CategoriaService;
import br.com.larcash.service.OrcamentoService;
import br.com.larcash.util.TokenUtil;
import jakarta.transaction.Transactional;

@RestController
@RequestMapping("/categorias")
public class CategoriaController {

	@Autowired
	private CategoriaService service;

	@Autowired
	private OrcamentoService orcamentoService;
	
	@Autowired
	private MapConverter converter;
	
	@Autowired
	private TokenUtil tokenUtil;
	
	@GetMapping("/ativas")
	public ResponseEntity<?> listarAtivos(){
		
		List<Categoria> categorias = service.listarPor(Status.A);
		
		if (categorias.isEmpty()) {
			return ResponseEntity.noContent().build();
		}

		return ResponseEntity.ok(converter.toJsonList(categorias));
		
	}
	
	@PutMapping("/me")
	@Transactional
	public ResponseEntity<?> alterar(
			@RequestHeader("Authorization")
			String authHeader, 
			@RequestBody
			NovoLimiteDaCategoria novoLimite){
		
		String loginDoToken = tokenUtil.extractLoginDo(authHeader);

		Orcamento orcamentoEncontrado = orcamentoService.buscarUltimoPor(loginDoToken);

		Categoria categoriaAtualizada = service.atualizarLimitePor(novoLimite.getIdDaCategoria(), 
				orcamentoEncontrado.getId(), novoLimite.getValor());

		return ResponseEntity.ok(converter.toJsonMap(categoriaAtualizada));

	}
	
	@PutMapping("/all/me")
	@Transactional
	public ResponseEntity<?> alterarTodas(
			@RequestHeader("Authorization")
			String authHeader, 
			@RequestBody
			List<NovoLimiteDaCategoria> novosLimites){
		
		String loginDoToken = tokenUtil.extractLoginDo(authHeader);

		Orcamento orcamentoEncontrado = orcamentoService.buscarUltimoPor(loginDoToken);
		
		List<Categoria> categoriasAtualizadas = service.atualizarTodosPor(
				orcamentoEncontrado.getId(), novosLimites);
				
		return ResponseEntity.ok(converter.toJsonList(categoriasAtualizadas));
		
	}
	
	@GetMapping("/me")
	public ResponseEntity<?> listarPor(
			@RequestHeader("Authorization")
			String authHeader){

		String loginDoToken = tokenUtil.extractLoginDo(authHeader);

		Orcamento orcamentoEncontrado = orcamentoService.buscarUltimoPor(loginDoToken);

		List<Categoria> categorias = service.listarPor(orcamentoEncontrado.getId());

		if (categorias.isEmpty()) {
			return ResponseEntity.noContent().build();
		}

		return ResponseEntity.ok(converter.toJsonList(categorias));
	
	}
	
}
