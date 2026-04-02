package br.com.larcash.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import br.com.larcash.entity.Categoria;
import br.com.larcash.enums.Status;
import br.com.larcash.repository.CategoriasRepository;
import jakarta.validation.constraints.NotNull;

@Service
@Validated
public class CategoriaService {

	@Autowired
	private CategoriasRepository repository;
	
	public List<Categoria> listarPor(
			@NotNull(message = "O status é obrigatorio")
			Status status){
		return repository.listarPor(status);
	}
	
}
