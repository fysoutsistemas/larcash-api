package br.com.larcash.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.larcash.converter.MapConverter;
import br.com.larcash.entity.CategoriaDoProduto;
import br.com.larcash.enums.Status;
import br.com.larcash.service.CategoriaDoProdutoService;

@RestController
@RequestMapping("/categs-produtos")
public class CategoriaDoProdController {

	@Autowired
	private CategoriaDoProdutoService service;
	
	@Autowired
	private MapConverter converter;
	
	@GetMapping("/ativas")
	public ResponseEntity<?> listarAtivas(){
		
		List<CategoriaDoProduto> categorias = service.listarPor(Status.A);
		
		if (categorias.isEmpty()) {
			return ResponseEntity.noContent().build();
		}

		return ResponseEntity.ok(converter.toJsonList(categorias));
	
	}
	
}
