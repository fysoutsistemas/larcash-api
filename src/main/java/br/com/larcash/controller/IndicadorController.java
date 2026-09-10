package br.com.larcash.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.larcash.converter.MapConverter;
import br.com.larcash.dto.indicadores.GastoOrcPorCateg;
import br.com.larcash.service.IndicadorService;
import br.com.larcash.util.TokenUtil;

@RestController
@RequestMapping("/indicadores")
public class IndicadorController {

	@Autowired
	private IndicadorService service;

	@Autowired
	private MapConverter converter;

	@Autowired
	private TokenUtil tokenUtil;

	@GetMapping("/gerais/ano/{ano}/mes/{mes}/me")
	public ResponseEntity<?> buscarUltimoPainelPor(
			@RequestHeader("Authorization") 
			String authHeader,
			@PathVariable("ano")
			Integer ano,
			@PathVariable("mes")
			Integer mes){

		Integer idDaFamilia = tokenUtil.extractIdDaFamiliaDo(authHeader);

		return ResponseEntity.ok(converter.toJsonMap(service
				.buscarIndicadores(idDaFamilia, ano, mes)));

	}
	
	@GetMapping("/gerais/orcamento/{idDoOrcamento}")
	public ResponseEntity<?> buscarPor(
			@RequestHeader("Authorization") 
			String authHeader,
			@PathVariable("idDoOrcamento")
			Integer idDoOrcamento){
		
		Integer idDaFamilia = tokenUtil.extractIdDaFamiliaDo(authHeader);

		List<GastoOrcPorCateg> gastos = service.listarGastosPor(idDoOrcamento, idDaFamilia);

		if (gastos.isEmpty()) {
			return ResponseEntity.noContent().build(); 
		}

		return ResponseEntity.ok(converter.toJsonList(gastos));

	}

}
