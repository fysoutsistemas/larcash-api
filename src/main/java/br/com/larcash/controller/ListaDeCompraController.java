package br.com.larcash.controller;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.larcash.converter.MapConverter;
import br.com.larcash.dto.DashboardDeCompras;
import br.com.larcash.dto.ItemDoCarrinho;
import br.com.larcash.dto.ListaDeCompraEncerrada;
import br.com.larcash.dto.ListaDeCompraSalva;
import br.com.larcash.dto.NovaListaDeCompra;
import br.com.larcash.dto.ResumoDaLista;
import br.com.larcash.entity.ListaDeCompra;
import br.com.larcash.enums.Confirmacao;
import br.com.larcash.enums.StatusDaLista;
import br.com.larcash.service.ListaDeCompraService;
import br.com.larcash.util.TokenUtil;
import jakarta.transaction.Transactional;

@RestController
@RequestMapping("/compras")
public class ListaDeCompraController {
	
	private final Integer QTDE_REGISTROS = 10,
			              PRIMEIRA_PAGINA = 0;
	
	@Autowired
	private ListaDeCompraService service;
	
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
			NovaListaDeCompra novaLista){

		String loginDoToken = tokenUtil.extractLoginDo(authHeader);

		ListaDeCompra listaSalva = service.inserir(novaLista, loginDoToken);

		return ResponseEntity.created(URI.create("/compras/id/" 
				+ listaSalva.getId())).build();

	}
	
	@PutMapping
	@Transactional
	public ResponseEntity<?> alterar(
			@RequestHeader("Authorization") 
			String authHeader,
			@RequestBody
			ListaDeCompraSalva listaSalva){
		
		String loginDoToken = tokenUtil.extractLoginDo(authHeader);
		
		ListaDeCompra listaAtualizada = service.alterar(listaSalva, loginDoToken);
		
		return ResponseEntity.ok(converter.toJsonMap(listaAtualizada, "usuario", "comprador"));
		
	}
	
	@GetMapping("/id/{id}")
	public ResponseEntity<?> buscarPor(
			@RequestHeader("Authorization") 
			String authHeader,
			@PathVariable("id")
			Integer id){
		
		Integer idDaFamilia = tokenUtil.extractIdDaFamiliaDo(authHeader);
		
		ListaDeCompra listaEncontrada = service.buscarPor(idDaFamilia, id);
		
		return ResponseEntity.ok(converter.toJsonMap(listaEncontrada, "usuario", "comprador"));
	}
	
	@GetMapping
	public ResponseEntity<?> listarTodas(
			@RequestHeader("Authorization") 
			String authHeader,
			@RequestParam("status")
			Optional<String> paramStatus,
			@RequestParam("pagina") 
			Optional<Integer> pagina){
		
		StatusDaLista status = paramStatus.isPresent() 
				? StatusDaLista.toEnum(paramStatus.get()) : null;
		
		Pageable paginacao = pagina.isPresent() ? PageRequest.of(pagina.get(), 
				QTDE_REGISTROS) : PageRequest.of(PRIMEIRA_PAGINA, QTDE_REGISTROS);
		
		Integer idDaFamilia = tokenUtil.extractIdDaFamiliaDo(authHeader);
		
		Page<ListaDeCompra> listas = service.listarPor(idDaFamilia, status, paginacao);
		
		if (listas.isEmpty()) {
			return ResponseEntity.noContent().build();
		}
		
		return ResponseEntity.ok(converter.toJsonList(listas, "usuario", "comprador"));
	}
	
	@GetMapping("/resumos")
	public ResponseEntity<?> listarResumos(
			@RequestHeader("Authorization") 
			String authHeader){
		
		Integer idDaFamilia = tokenUtil.extractIdDaFamiliaDo(authHeader);
		
		List<ResumoDaLista> resumos = service.listarResumosPor(idDaFamilia);
		
		return ResponseEntity.ok(converter.toJsonList(resumos));
		
	}
	
	@PatchMapping("/{id}/ativa")
	public ResponseEntity<?> ativar(
			@RequestHeader("Authorization") 
			String authHeader,
			@PathVariable("id")
			Integer id){
		
		String loginDoComprador = tokenUtil.extractLoginDo(authHeader);
		
		ListaDeCompra listaAtualizada = service.atualizarStatusPor(loginDoComprador, id, Confirmacao.S);
		
		return ResponseEntity.ok(converter.toJsonMap(listaAtualizada, "usuario", "comprador"));
		
	}
	
	@PatchMapping("/{id}/inativa")
	public ResponseEntity<?> inativar(
			@RequestHeader("Authorization") 
			String authHeader,
			@PathVariable("id")
			Integer id){
		
		String loginDoComprador = tokenUtil.extractLoginDo(authHeader);
		
		ListaDeCompra listaAtualizada = service.atualizarStatusPor(loginDoComprador, id, Confirmacao.N);
		
		return ResponseEntity.ok(converter.toJsonMap(listaAtualizada, "usuario", "comprador"));
		
	}
	
	@PutMapping("/encerrar")
	@Transactional
	public ResponseEntity<?> encerrarLista(
			@RequestHeader("Authorization") 
			String authHeader,
			@RequestBody
			ListaDeCompraEncerrada lista){
		
		String loginDoComprador = tokenUtil.extractLoginDo(authHeader);
		
		lista.setLoginDoComprador(loginDoComprador);
		
		this.service.encerrar(lista);
		
		return ResponseEntity.ok().build();
	}
	
	@PutMapping("/{id-lista}/carrinho")
	@Transactional
	public ResponseEntity<?> adicionarNoCarrinhoPor(
			@RequestHeader("Authorization") 
			String authHeader,
			@PathVariable("id-lista")
			Integer idDaLista,
			@RequestBody
			ItemDoCarrinho itemDoCarrinho){
		
		String loginDoComprador = tokenUtil.extractLoginDo(authHeader);
		
		this.service.adicionarNoCarrinhoPor(idDaLista, itemDoCarrinho, loginDoComprador);
		
		return ResponseEntity.ok().build();
		
	}
	
	@DeleteMapping("/{id-lista}/produto/{id-produto}/carrinho")
	@Transactional
	public ResponseEntity<?> retirarDoCarrinhoPor(
			@RequestHeader("Authorization") 
			String authHeader,
			@PathVariable("id-lista")
			Integer idDaLista,
			@PathVariable("id-produto")
			Integer idDoProduto){
		
		String loginDoComprador = tokenUtil.extractLoginDo(authHeader);
		
		this.service.retirarDoCarrinhoPor(idDaLista, idDoProduto, loginDoComprador);
		
		return ResponseEntity.ok().build();
		
	}
	
	@GetMapping("/dashboard/me")
	public ResponseEntity<?> buscarDashboard(
			@RequestHeader("Authorization") 
			String authHeader){
		
		final Integer PERIODO_DIAS = 30;
		
		Integer idDaFamilia = tokenUtil.extractIdDaFamiliaDo(authHeader);
		
		DashboardDeCompras dashboard = service.buscarDashboardPor(idDaFamilia, PERIODO_DIAS);
		
		return ResponseEntity.ok(converter.toJsonMap(dashboard));
		
	}
	
}
