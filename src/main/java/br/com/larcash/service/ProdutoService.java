package br.com.larcash.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.google.common.base.Preconditions;

import br.com.larcash.dto.NovoProduto;
import br.com.larcash.entity.CategoriaDoProduto;
import br.com.larcash.entity.Produto;
import br.com.larcash.entity.Usuario;
import br.com.larcash.enums.Status;
import br.com.larcash.exception.RegistroNaoEncontradoException;
import br.com.larcash.repository.ProdutosRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Service
@Validated
public class ProdutoService {

	@Autowired
	private ProdutosRepository repository;
	
	@Autowired	
	private CategoriaDoProdutoService categoriaService;
	
	@Autowired
	private UsuarioService usuarioService;

	public Produto inserir(
			@Valid
			@NotNull(message = "O novo produto não pode ser nulo")
			NovoProduto novoProduto,
			@NotBlank(message = "O login do criado é obrigatório")
			String login) {
		
		CategoriaDoProduto categoriaEncontrada = categoriaService
				.buscarPor(novoProduto.getCategoria().getId());
		
		Preconditions.checkArgument(categoriaEncontrada.isAtiva(), 
				"A categoria do produto deve estar ativa");
		
		Usuario usuarioEncontrado = usuarioService.buscarPorLogin(login);
		
		Produto produto = new Produto();
		produto.setDescricao(novoProduto.getDescricao());
		produto.setFoto(novoProduto.getFoto());
		produto.setPrecoEstimado(novoProduto.getPrecoEstimado());
		produto.setUsuario(usuarioEncontrado);
		produto.setFamilia(usuarioEncontrado.getFamilia());
		produto.setCategoria(categoriaEncontrada);
		
		return repository.save(produto);

	}
	
	public Produto atualizarStatusPor(
			@NotNull(message = "O id da familia é obrigatório")
			@Positive(message = "O id da família deve ser positivo")
			Integer idDaFamilia,
			@NotNull(message = "O id do produto é obrigatório")
			@Positive(message = "O id do produto deve ser positivo")
			Integer idDoProduto,
			@NotNull(message = "O status é obrigatório")
			Status status) {
		
		Produto produtoEncontrado = buscarPor(idDaFamilia, idDoProduto);
		
		if (status == Status.A) {
			Preconditions.checkArgument(!produtoEncontrado.isAtivo(), "O produto já está ativo");
		}else {
			Preconditions.checkArgument(produtoEncontrado.isAtivo(), "O produto já está inativo");
		}
		
		produtoEncontrado.setStatus(status);

		this.repository.save(produtoEncontrado);

		return produtoEncontrado;

	}
	
	public List<Produto> listarAtivosPor(
			@NotNull(message = "O id da familia é obrigatório")
			@Positive(message = "O id da família deve ser positivo")
			Integer idDaFamilia){
		
		List<Produto> produtos = repository.listarPor(idDaFamilia, Status.A);

		produtos.forEach(p -> {
			p.setLoginDoCriador(p.getLoginDoCriador());
		});

		return produtos;

	}
	
	public Produto buscarPor(
			@NotNull(message = "O id da familia é obrigatório")
			@Positive(message = "O id da família deve ser positivo")
			Integer idDaFamilia,
			@NotNull(message = "O id do produto é obrigatório")
			@Positive(message = "O id do produto deve ser positivo")
			Integer idDoProduto) {
		
		Produto produtoEncontrado = repository.buscarPor(idDaFamilia, idDoProduto);
		
		Optional.ofNullable(produtoEncontrado)
			.orElseThrow(() -> new RegistroNaoEncontradoException(
	    			"Não existe produto vinculado aos parâmetros informados"));
		
		produtoEncontrado.setLoginDoCriador(produtoEncontrado.getLogin());
		
		return produtoEncontrado;
		
	}
	
	public Produto buscarAtivoPor(
			@NotNull(message = "O id da familia é obrigatório")
			@Positive(message = "O id da família deve ser positivo")
			Integer idDaFamilia,
			@NotNull(message = "O id do produto é obrigatório")
			@Positive(message = "O id do produto deve ser positivo")
			Integer idDoProduto) {

		Produto produtoEncontrado = buscarPor(idDaFamilia, idDoProduto);

		Preconditions.checkArgument(produtoEncontrado.isAtivo(), 
				"O produto vinculado ao id='" + idDoProduto + "' deve estar ativo");
		
		return produtoEncontrado;

	}
	
	public Produto atualizarPrecoPor(
			@NotNull(message = "O id da familia é obrigatório")
			@Positive(message = "O id da família deve ser positivo")
			Integer idDaFamilia,
			@NotNull(message = "O id do produto é obrigatório")
			@Positive(message = "O id do produto deve ser positivo")
			Integer idDoProduto,
			BigDecimal novoPreco) {

		Produto produtoEncontrado = buscarAtivoPor(idDaFamilia, idDoProduto);

		produtoEncontrado.setPrecoEstimado(novoPreco);

		this.repository.save(produtoEncontrado);

		return produtoEncontrado;

	}

}
