package br.com.larcash.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.google.common.base.Preconditions;

import br.com.larcash.entity.CategoriaDoProduto;
import br.com.larcash.enums.Status;
import br.com.larcash.exception.RegistroNaoEncontradoException;
import br.com.larcash.repository.CategoriasDoProdRepository;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Service
@Validated
public class CategoriaDoProdutoService {

	@Autowired
	private CategoriasDoProdRepository repository;
	
	public List<CategoriaDoProduto> listarPor(
			@NotNull(message = "O status é obrigatório")
			Status status){
		return repository.listarPor(status);
	}

	public CategoriaDoProduto buscarPor(
			@NotNull(message = "O id da categoria é obrigatório")
			@Positive(message = "O id da categoria deve ser positivo")
			Integer id) {
		
		CategoriaDoProduto categoriaEncontrada = repository.buscarPor(id);
		
		Optional.ofNullable(categoriaEncontrada)
				.orElseThrow(() -> new RegistroNaoEncontradoException(
						"Não existe categoria vinculada ao id informado"));
		
		return categoriaEncontrada;
		
	}
	
	public CategoriaDoProduto buscarAtivaPor(Integer id) {
		
		CategoriaDoProduto categoriaEncontrada = buscarPor(id);
		
		Preconditions.checkArgument(categoriaEncontrada.isAtiva(), 
				"A categoria do produto deve estar ativa");
		
		return categoriaEncontrada;
		
	}
	
}
