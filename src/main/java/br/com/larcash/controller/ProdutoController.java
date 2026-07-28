package br.com.larcash.controller;

import java.net.URI;
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
import br.com.larcash.dto.NovoProduto;
import br.com.larcash.entity.Produto;
import br.com.larcash.enums.Status;
import br.com.larcash.service.ProdutoService;
import br.com.larcash.util.TokenUtil;
import jakarta.transaction.Transactional;

@RestController
@RequestMapping("/produtos")
public class ProdutoController {
	
	@Autowired
	private ProdutoService service;
	
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
			NovoProduto novoProduto){
		
		String loginDoToken = tokenUtil.extractLoginDo(authHeader);
		
		Produto produtoSalvo = service.inserir(novoProduto, loginDoToken);
		
		return ResponseEntity.created(URI.create("/produtos/id/" 
				+ produtoSalvo.getId())).build();
		
	}

	@GetMapping("/id/{id}")
	public ResponseEntity<?> buscarPor(
			@RequestHeader("Authorization") 
			String authHeader,
			@PathVariable("id")
			Integer id){

		Integer idDaFamilia = tokenUtil.extractIdDaFamiliaDo(authHeader);

		Produto produtoEncontrado = service.buscarPor(idDaFamilia, id);

		return ResponseEntity.ok(converter.toJsonMap(produtoEncontrado, "usuario"));

	}
	
	@GetMapping
	public ResponseEntity<?> listarTodos(
			@RequestHeader("Authorization") 
			String authHeader){

		Integer idDaFamilia = tokenUtil.extractIdDaFamiliaDo(authHeader);

		List<Produto> produtos = service.listarAtivosPor(idDaFamilia);

		if (produtos.isEmpty()) {
			return ResponseEntity.noContent().build();
		}

		return ResponseEntity.ok(converter.toJsonList(produtos, "usuario"));

	}
	
	@PatchMapping("/{id}/ativo")
	public ResponseEntity<?> ativar(
			@RequestHeader("Authorization") 
			String authHeader,
			@PathVariable("id")
			Integer id){
		
		Integer idDaFamilia = tokenUtil.extractIdDaFamiliaDo(authHeader);
		
		Produto produtoAtualizado = service.atualizarStatusPor(idDaFamilia, id, Status.A);
		
		return ResponseEntity.ok(converter.toJsonMap(produtoAtualizado, "usuario"));
		
	}
	
	@PatchMapping("/{id}/inativo")
	public ResponseEntity<?> inativar(
			@RequestHeader("Authorization") 
			String authHeader,
			@PathVariable("id")
			Integer id){
		
		Integer idDaFamilia = tokenUtil.extractIdDaFamiliaDo(authHeader);
		
		Produto produtoAtualizado = service.atualizarStatusPor(idDaFamilia, id, Status.I);
		
		return ResponseEntity.ok(converter.toJsonMap(produtoAtualizado, "usuario"));
		
	}

}
