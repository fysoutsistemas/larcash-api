package br.com.larcash.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.google.common.base.Preconditions;

import br.com.larcash.dto.NovoProduto;
import br.com.larcash.dto.ProdutoSalvo;
import br.com.larcash.entity.CategoriaDoProduto;
import br.com.larcash.entity.Produto;
import br.com.larcash.entity.Usuario;
import br.com.larcash.enums.Status;
import br.com.larcash.exception.RegistroNaoEncontradoException;
import br.com.larcash.repository.ProdutosRepository;
import br.com.larcash.util.FileUtil;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Service
@Validated
public class ProdutoService {

	private final BigDecimal TAMANHO_MAXIMO = new BigDecimal(600000);//600kb	
	
	@Autowired
	private ProdutosRepository repository;
	
	@Autowired	
	private CategoriaDoProdutoService categoriaService;
	
	@Autowired
	private UsuarioService usuarioService;
	
	@Autowired
	private FileUtil fileUtil;

	public Produto inserir(
			@Valid
			@NotNull(message = "O novo produto não pode ser nulo")
			NovoProduto novoProduto,
			@NotBlank(message = "O login do criado é obrigatório")
			String login) {
		
		if (!Strings.isBlank(novoProduto.getFoto())) {

			Preconditions.checkArgument(fileUtil.isImg(novoProduto.getFoto()), 
					"Formato de arquivo inválido");			
			
			BigDecimal tamanhoDaFoto = fileUtil.getSize(novoProduto.getFoto());

			Preconditions.checkArgument(tamanhoDaFoto.compareTo(TAMANHO_MAXIMO) < 0, 
					"O tamanho máximo da foto não deve ser maior que 500kb");

		}

		CategoriaDoProduto categoriaEncontrada = categoriaService
				.buscarAtivaPor(novoProduto.getCategoria().getId());
		
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
	
	public Produto alterar(
			@Valid
			@NotNull(message = "O produto salvo é obrigatório")
			ProdutoSalvo produtoSalvo,
			@NotBlank(message = "O login do criado é obrigatório")
			String login) {
		
		if (!Strings.isBlank(produtoSalvo.getFoto())) {
			
			Preconditions.checkArgument(fileUtil.isImg(produtoSalvo.getFoto()), 
					"Formato de arquivo inválido");

			BigDecimal tamanhoDaFoto = fileUtil.getSize(produtoSalvo.getFoto());

			Preconditions.checkArgument(tamanhoDaFoto.compareTo(TAMANHO_MAXIMO) < 0, 
					"O tamanho máximo da foto não deve ser maior que 500kb");

		}
		
		CategoriaDoProduto categoriaEncontrada = categoriaService
				.buscarAtivaPor(produtoSalvo.getCategoria().getId());
		
		Usuario usuarioEncontrado = usuarioService.buscarPorLogin(login);
		
		Produto produtoParaEdicao = buscarAtivoPor(usuarioEncontrado
				.getIdDaFamilia(), produtoSalvo.getId());
		
		produtoParaEdicao.setDescricao(produtoSalvo.getDescricao());
		produtoParaEdicao.setFoto(produtoSalvo.getFoto());
		produtoParaEdicao.setPrecoEstimado(produtoSalvo.getPrecoEstimado());
		produtoParaEdicao.setCategoria(categoriaEncontrada);
		
		return repository.save(produtoParaEdicao);

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
