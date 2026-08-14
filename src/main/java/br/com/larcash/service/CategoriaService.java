package br.com.larcash.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.google.common.base.Preconditions;

import br.com.larcash.dto.NovoLimiteDaCategoria;
import br.com.larcash.entity.Categoria;
import br.com.larcash.entity.CategoriaDoOrcamento;
import br.com.larcash.entity.Orcamento;
import br.com.larcash.entity.composite.CategoriaDoOrcamentoId;
import br.com.larcash.enums.Status;
import br.com.larcash.exception.RegistroNaoEncontradoException;
import br.com.larcash.repository.CategoriasDoOrctoRepository;
import br.com.larcash.repository.CategoriasRepository;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Service
@Validated
public class CategoriaService {

	@Autowired
	private CategoriasRepository repository;
	
	@Autowired
	private CategoriasDoOrctoRepository categsDaFamiliaRepository;	
	
	@Autowired
	private OrcamentoService orcamentoService;
	
	public List<Categoria> listarPor(
			@NotNull(message = "O status é obrigatorio")
			Status status){
		return repository.listarPor(status);
	}
	
	public Categoria buscarPor(
			@NotNull(message = "O id da categoria é obrigatório")
			@Positive(message = "O id da categoria deve ser positivo")
			Integer id) {
		
		Categoria categoriaEncontrada = repository.buscarPor(id);
		
		Optional.ofNullable(categoriaEncontrada)
				.orElseThrow(() -> new RegistroNaoEncontradoException(
						"Não existe categoria vinculada ao id informado"));
		
		return categoriaEncontrada;
		
	}
	
	public CategoriaDoOrcamento buscarPor(
			@NotNull(message = "O id do orçamento é obrigatório")
			@Positive(message = "O id do orçamento deve ser positivo")
			Integer idDoOrcamento,
			@NotBlank(message = "O nome da categoria é obrigatória")
			String nome) {

		CategoriaDoOrcamento categoriaEncontrada = categsDaFamiliaRepository
				.buscarPor(idDoOrcamento, nome);

		Optional.ofNullable(categoriaEncontrada)
				.orElseThrow(() -> new RegistroNaoEncontradoException(
						"Não existe categoria vinculada aos parâmetros informados"));

		return categoriaEncontrada;

	}
	
	public BigDecimal buscarLimitePor(
			@NotNull(message = "O id da família é obrigatório")
			@Positive(message = "O id da família deve ser positivo")
			Integer idDaFamilia,
			@NotNull(message = "O id da categoria é obrigatório")
			@Positive(message = "O id da categoria deve ser positivo")
			Integer idDaCategoria) {
		return categsDaFamiliaRepository.buscarLimitePor(idDaFamilia, idDaCategoria);
	}
	
	public Categoria atualizarLimitePor(			
			@NotNull(message = "O id da categoria é obrigatório")
			@Positive(message = "O id da categoria deve ser positivo")
			Integer idDaCategoria,
			@NotNull(message = "O id do orçamento é obrigatório")
			@Positive(message = "O id da orçamento deve ser positivo")
			Integer idDoOrcamento, 
			@NotNull(message = "O limite é obrigatório")
			@Positive(message = "O limite deve ser positivo")
			BigDecimal limite) {
		
		this.buscarPor(idDaCategoria);
		
		this.orcamentoService.buscarAtivoPor(idDoOrcamento);
		
		this.categsDaFamiliaRepository.atualizarLimitePor(idDaCategoria, idDoOrcamento, limite);
		
		CategoriaDoOrcamento categoriaDaFamilia = categsDaFamiliaRepository
				.buscarPor(idDaCategoria, idDoOrcamento);
		
		Categoria categoriaAlterada = categoriaDaFamilia.getCategoria();
		categoriaAlterada.setLimite(categoriaDaFamilia.getLimite());
		categoriaAlterada.setStatus(categoriaDaFamilia.getStatus());

		return categoriaAlterada;

	}
	
	public List<Categoria> atualizarTodosPor(
			@NotNull(message = "O id do orçamento é obrigatório")
			@Positive(message = "O id da orçamento deve ser positivo")
			Integer idDoOrcamento,
			@NotEmpty(message = "A listagem com os limites deve cointer ao menos 1 item")
			List<NovoLimiteDaCategoria> limites){
		
		List<Categoria> categsDoOrcamento = listarPor(idDoOrcamento);

		Preconditions.checkArgument(categsDoOrcamento.size() == limites.size(), 
				"Os limites informados não coincidem com a quantidade de categorias criadas");

		//Valida a listagem de categorias pois os todos os limites devem ser atualizados
		//e estarem presentes na listagem de limites recebidas por parametro
		for (Categoria categ : categsDoOrcamento) {

			boolean isPresente = false;

			for (NovoLimiteDaCategoria novoLimite : limites) {
				
				if (categ.getId().equals(novoLimite.getIdDaCategoria())) {
					categ.setLimite(novoLimite.getValor());
					isPresente = true;
					break;
				}

			}

			Preconditions.checkArgument(isPresente, "O limite da categoria '" 
					+ categ.getId() + "' não foi informado");

		}

		//Realiza a atualização do limite de todas as categorias
		for (NovoLimiteDaCategoria novoLimite : limites) {
			this.categsDaFamiliaRepository.atualizarLimitePor(novoLimite.getIdDaCategoria(), 
					idDoOrcamento, novoLimite.getValor());
		}

		this.orcamentoService.marcarCategsComoConfiguradasPor(idDoOrcamento);
		
		//Retorna uma lista de categorias atualizadas ao final
		return categsDoOrcamento;		

	}
	
	public List<Categoria> listarPor(
			@NotNull(message = "O id do orçamento é obrigatório")
			@Positive(message = "O id da orçamento deve ser positivo")
			Integer idDoOrcamento){
		
		this.orcamentoService.buscarPor(idDoOrcamento);

		List<CategoriaDoOrcamento> categsDaFamilia = categsDaFamiliaRepository.listarPor(idDoOrcamento);

		List<Categoria> categorias = new ArrayList<>();

		for (CategoriaDoOrcamento categDaFamilia : categsDaFamilia) {
			Categoria categoria = categDaFamilia.getCategoria();
			categoria.setLimite(categDaFamilia.getLimite());
			categoria.setStatus(categDaFamilia.getStatus());
			categorias.add(categoria);
		}
		
		return categorias;

	}
	
	public void vincularCategoriasNo(
			@NotNull(message = "O orçamento não pode ser nulo")
			Orcamento orcamento) {
		
		Orcamento orcamentoSalvo = orcamentoService.buscarPor(orcamento.getId());
		
		List<Categoria> categorias = listarPor(Status.A);
		
		for (Categoria categoria : categorias) {

			CategoriaDoOrcamentoId id = new CategoriaDoOrcamentoId();
			id.setIdDaCategoria(categoria.getId());
			id.setIdDoOrcamento(orcamentoSalvo.getId());

			CategoriaDoOrcamento categDaFamilia = new CategoriaDoOrcamento();
			categDaFamilia.setId(id);
			categDaFamilia.setCategoria(categoria);
			categDaFamilia.setOrcamento(orcamentoSalvo);

			this.categsDaFamiliaRepository.save(categDaFamilia);

		}
		
	}
	
}
