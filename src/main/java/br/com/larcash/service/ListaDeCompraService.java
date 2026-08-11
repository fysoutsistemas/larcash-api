package br.com.larcash.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.google.common.base.Preconditions;

import br.com.larcash.dto.ItemDaListaResumido;
import br.com.larcash.dto.ItemDoCarrinho;
import br.com.larcash.dto.ListaDeCompraSalva;
import br.com.larcash.dto.NovaListaDeCompra;
import br.com.larcash.dto.ResumoDaLista;
import br.com.larcash.entity.ItemDaLista;
import br.com.larcash.entity.ListaDeCompra;
import br.com.larcash.entity.Produto;
import br.com.larcash.entity.Usuario;
import br.com.larcash.enums.Confirmacao;
import br.com.larcash.enums.StatusDaLista;
import br.com.larcash.exception.RegistroNaoEncontradoException;
import br.com.larcash.repository.ItensDaListaRepository;
import br.com.larcash.repository.ListasDeCompraRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Service
@Validated
public class ListaDeCompraService {

	@Autowired
	private ListasDeCompraRepository repository;
	
	@Autowired
	private ItensDaListaRepository itensRepository;
	
	@Autowired
	private ProdutoService produtoService;
	
	@Autowired
	private UsuarioService usuarioService;

	public ListaDeCompra inserir(
			@Valid
			@NotNull(message = "A nova lista não pode ser nula")
			NovaListaDeCompra novaLista,
			@NotBlank(message = "O login do criador é obrigatório")
			String loginCriador) {

		this.validar(novaLista.getItens());

		Usuario usuario = usuarioService.buscarPorLogin(loginCriador);

		ListaDeCompra lista = new ListaDeCompra();
		lista.setNome(novaLista.getNome());
		lista.setUsuario(usuario);
		lista.setFamilia(usuario.getFamilia());
		lista.setQtde(novaLista.getItens().size());

		for (ItemDaListaResumido novoItem : novaLista.getItens()) {

			Produto produto = produtoService.buscarAtivoPor(usuario
					.getIdDaFamilia(), novoItem.getIdDoProduto());

			lista.adicionar(produto, novoItem.getQtde(), novoItem.getOrdem());

		}

		ListaDeCompra listaSalva = repository.save(lista);

		return buscarPor(usuario.getIdDaFamilia(), listaSalva.getId());

	}

	public ListaDeCompra alterar(
			@Valid
			@NotNull(message = "A lista salva não pode ser nula")
			ListaDeCompraSalva listaSalva,
			@NotBlank(message = "O login do alterador é obrigatório")
			String loginAlterador) {

		this.validar(listaSalva.getItens());			
		
		Usuario usuario = usuarioService.buscarPorLogin(loginAlterador);
		
		ListaDeCompra lista = buscarPor(usuario.getIdDaFamilia(), listaSalva.getId());
		
		Preconditions.checkArgument(lista.isNova(), "A edição só é possível quando a lista é nova");

		lista.setNome(listaSalva.getNome());
		lista.setQtde(listaSalva.getItens().size());

		//Limpa a lista encontrada na consulta
		lista.removerItens();

		//Remove os itens do banco de dados
		this.itensRepository.removerItensPor(listaSalva.getId());
		
		for (ItemDaListaResumido itemSalvo : listaSalva.getItens()) {

			Produto produto = produtoService.buscarAtivoPor(usuario
					.getIdDaFamilia(), itemSalvo.getIdDoProduto());

			lista.adicionar(produto, itemSalvo.getQtde(), itemSalvo.getOrdem());

		}

		ListaDeCompra listaAtualizada = repository.save(lista);

		return buscarPor(usuario.getIdDaFamilia(), listaAtualizada.getId());

	}
	
	public ItemDaLista adicionarNoCarrinhoPor(
			@NotNull(message = "O id da lista é obrigatório")
			@Positive(message = "O id da lista deve ser positivo")
			Integer idDaLista,
			@Valid
			@NotNull(message = "O item é obrigatório")
			ItemDoCarrinho itemDoCarrinho,
			@NotBlank(message = "O login do comprador é obrigatório")
			String loginDoComprador) {
		
		Usuario comprador = usuarioService.buscarPorLogin(loginDoComprador);
		
		ItemDaLista itemDaLista = buscarPor(comprador.getIdDaFamilia(), 
				idDaLista, itemDoCarrinho.getIdDoProduto());

		Preconditions.checkArgument(!itemDaLista.isNoCarrinho(), "O item já está no carrinho");		
		
		ListaDeCompra listaDoItem = itemDaLista.getListaDeCompra();		
		
		Preconditions.checkArgument(!listaDoItem.isEncerrada(), "A lista já foi ENCERRADA");
				
		//Subtrai do total da compra e estimado o subtotal do item antes de atualiza-lo
		BigDecimal totalEstimado = listaDoItem.getTotalEstimado().subtract(itemDaLista.getSubtotal());		
		listaDoItem.setTotalEstimado(totalEstimado);

		listaDoItem.setUsuario(comprador);
		
		if (listaDoItem.isNova()) {
			listaDoItem.setStatus(StatusDaLista.INICIADA);
		}
		
		itemDaLista.setFlagNoCarrinho(Confirmacao.S);
		
		BigDecimal subtotal = itemDoCarrinho.getPreco()
				.multiply(itemDoCarrinho.getQtde())
				.setScale(2, RoundingMode.HALF_EVEN);
		
		itemDaLista.setPreco(itemDoCarrinho.getPreco());
		
		itemDaLista.setQtde(itemDoCarrinho.getQtde());
		
		itemDaLista.setSubtotal(subtotal);
		
		this.itensRepository.save(itemDaLista);
		
		//Recompõe o total da compra e estimado a partir do  
		//total da compra baseada no item do carrinho
		BigDecimal totalDaCompra = listaDoItem.getTotalDaCompra().add(subtotal);		
		listaDoItem.setTotalDaCompra(totalDaCompra);
		
		totalEstimado = listaDoItem.getTotalEstimado().add(subtotal);
		listaDoItem.setTotalEstimado(totalEstimado);
		
		listaDoItem.setDifDeTotais(totalEstimado.subtract(totalDaCompra));
		
		this.repository.atualizarTotaisPor(comprador.getIdDaFamilia(), idDaLista, 
				totalDaCompra, totalEstimado, listaDoItem.getDifDeTotais(), 
				loginDoComprador);
		
		this.produtoService.atualizarPrecoPor(comprador.getIdDaFamilia(), 
				itemDaLista.getIdDoProduto(), itemDoCarrinho.getPreco());
		
		return itemDaLista;
		
	}
	
	public ItemDaLista retirarDoCarrinhoPor(
			@NotNull(message = "O id da lista é obrigatório")
			@Positive(message = "O id da lista deve ser positivo")
			Integer idDaLista,
			@NotNull(message = "O id do produto é obrigatório")
			@Positive(message = "O id do produto deve ser positivo")
			Integer idDoProduto,
			@NotBlank(message = "O login do comprador é obrigatório")
			String loginDoComprador) {
		
		Usuario comprador = usuarioService.buscarPorLogin(loginDoComprador);
		
		ItemDaLista itemDaLista = buscarPor(comprador.getIdDaFamilia(), 
				idDaLista, idDoProduto);
		
		Preconditions.checkArgument(itemDaLista.isNoCarrinho(), "O item já está fora no carrinho");		
		
		ListaDeCompra listaDoItem = itemDaLista.getListaDeCompra();		
		
		Preconditions.checkArgument(listaDoItem.isIniciada(), "A lista não pode ser NOVA ou ENCERRADA");
		
		BigDecimal totalDaCompra = listaDoItem.getTotalDaCompra().subtract(itemDaLista.getSubtotal());
		
		listaDoItem.setDifDeTotais(listaDoItem.getTotalEstimado().subtract(totalDaCompra));			
		
		listaDoItem.setTotalDaCompra(totalDaCompra);
		
		itemDaLista.setFlagNoCarrinho(Confirmacao.N);
		
		Integer qtdeDeItensNoCarrinho =  itensRepository.contarItensNoCarrinhoPor(idDaLista);
		
		if (qtdeDeItensNoCarrinho == 0) {
			this.repository.atualizarStatusPor(comprador.getIdDaFamilia(), 
					idDaLista, StatusDaLista.NOVA);
		}
		
		this.itensRepository.atualizarStatusNoCarrinhoPor(listaDoItem.getId(), 
				itemDaLista.getProduto().getId(), itemDaLista.getFlagNoCarrinho());
		
		this.repository.atualizarTotaisPor(comprador.getIdDaFamilia(), idDaLista, 
				listaDoItem.getTotalDaCompra(), listaDoItem.getTotalEstimado(), 
				listaDoItem.getDifDeTotais(), loginDoComprador);			
		
		return itemDaLista;

	}
	
	private ItemDaLista buscarPor(Integer idDaFamilia, Integer idDaLista, Integer idDoProduto) {
		
		ItemDaLista itemDaLista = itensRepository.buscarPor(idDaFamilia, idDaLista, idDoProduto);
		
		Optional.ofNullable(itemDaLista).orElseThrow(() -> new RegistroNaoEncontradoException(
				"Não existe item para lista com os parâmetros informados"));
		
		return itemDaLista;

	}
	
	private void validar(List<ItemDaListaResumido> itensDaLista) {
		this.validarRepeticaoDe(itensDaLista);
		this.validarOrdenacaoDos(itensDaLista);
	}

	private void validarOrdenacaoDos(List<ItemDaListaResumido> itensDaLista) {
		
		List<Integer> ordenacao = itensDaLista.stream().map(i -> i.getOrdem())
				.collect(Collectors.toList());
		
		Collections.sort(ordenacao);
		
		for (int indice = 0; indice < ordenacao.size(); indice++) {
			
			boolean isUltimo = indice == ordenacao.size() - 1;
			
			if (!isUltimo) {
				
				int atual = ordenacao.get(indice);
				
				int proximo = ordenacao.get(indice + 1);
				
				boolean isEmSequencia = proximo - atual == 1;
				
				Preconditions.checkArgument(isEmSequencia, "A ordenação é inválida");
				
			}
			
		}
		
	}
	
	private void validarRepeticaoDe(List<ItemDaListaResumido> novosItens) {

		for (ItemDaListaResumido novoItem : novosItens) {
			
			int qtdeDeOcorrencias = 0;
			
			for (ItemDaListaResumido outroItem : novosItens) {
				
				if (novoItem.getIdDoProduto().equals(outroItem.getIdDoProduto())) {
					qtdeDeOcorrencias++;
				}
				
			}
			
			Preconditions.checkArgument(qtdeDeOcorrencias == 1, 
					"O produto '" + novoItem.getIdDoProduto() + "' está repetido na lista");
			
		}
		
	}
	
	public ListaDeCompra buscarPor(
			@NotNull(message = "O id da familia é obrigatório")
			@Positive(message = "O id da família deve ser positivo")
			Integer idDaFamilia,
			@NotNull(message = "O id da lista é obrigatório")
			@Positive(message = "O id da lista deve ser positivo")
			Integer idDaLista) {
		
		ListaDeCompra listaEncontrada = repository.buscarPor(idDaFamilia, idDaLista);
		
		Optional.ofNullable(listaEncontrada)
			.orElseThrow(() -> new RegistroNaoEncontradoException(
					"Não existe lista vinculada ao id informado"));
		
		return listaEncontrada;
		
	}
	
	public ListaDeCompra atualizarStatusPor(
			@NotBlank(message = "O login do comprador é obrigatório")
			String loginDoComprador,
			@NotNull(message = "O id da lista é obrigatório")
			@Positive(message = "O id da lista deve ser positivo")
			Integer idDaLista,
			@NotNull(message = "O indicador da ativação da lista é obrigatório")
			Confirmacao flAtivo) {
		
		Usuario comprador = usuarioService.buscarPorLogin(loginDoComprador);

		ListaDeCompra listaEncontrada = buscarPor(comprador.getIdDaFamilia(), idDaLista);

		if (flAtivo == Confirmacao.S) {
			Preconditions.checkArgument(listaEncontrada.getFlAtivo() == Confirmacao.N, 
					"A lista já está ativa");
		}else {
			Preconditions.checkArgument(listaEncontrada.getFlAtivo() == Confirmacao.S, 
					"A lista ja está inativa");
		}
		
		listaEncontrada.setComprador(comprador);

		listaEncontrada.setFlAtivo(flAtivo);

		this.repository.save(listaEncontrada);

		return listaEncontrada;

	}
	
	public ListaDeCompra atualizarStatusPor(
			@NotBlank(message = "O login do comprador é obrigatório")
			String loginDoComprador,
			@NotNull(message = "O id da lista é obrigatório")
			@Positive(message = "O id da lista deve ser positivo")
			Integer idDaLista,
			StatusDaLista status) {
		
		Usuario comprador = usuarioService.buscarPorLogin(loginDoComprador);

		ListaDeCompra listaEncontrada = buscarPor(comprador.getIdDaFamilia(), idDaLista);
		
		Preconditions.checkArgument(listaEncontrada.getFlAtivo() == Confirmacao.S, 
				"A lista está inativa");
		
		listaEncontrada.setComprador(comprador);
		
		listaEncontrada.setStatus(status);
		
		this.repository.save(listaEncontrada);

		return listaEncontrada;

	}
	
	public Page<ListaDeCompra> listarPor(
			@NotNull(message = "O id da família é obrigatório")
			@Positive(message = "O id da família deve ser positivo")
			Integer idDaFamilia,
			StatusDaLista status,
			@NotNull(message = "A paginação é obrigatória")
			Pageable paginacao){
		return repository.listarPor(idDaFamilia, status, paginacao);
	}
	
	public List<ResumoDaLista> listarResumosPor(
			@NotNull(message = "O id da família é obrigatório")
			@Positive(message = "O id da família deve ser positivo")
			Integer idDaFamilia){
		
		List<ResumoDaLista> todosStatus = new ArrayList<>();
		
		List<ResumoDaLista> resumosEncontrados = repository.listarResumosPor(idDaFamilia);
		
		for (StatusDaLista status : StatusDaLista.values()) {
			
			boolean isPossuiContagem = false;
			
			for (ResumoDaLista resumo : resumosEncontrados) {
				
				if (resumo.getStatus() == status) {
					todosStatus.add(resumo);
					isPossuiContagem = true;
					break;
				}
				
			}
			
			if (!isPossuiContagem) {
				//Cria um resumo zerado pois ele não possui contagem na consulta
				todosStatus.add(new ResumoDaLista(status, 0L));
			}
			
		}
		
		return todosStatus;	

	}

}
