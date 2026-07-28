package br.com.larcash.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import br.com.larcash.entity.CategoriaDoProduto;
import br.com.larcash.enums.Status;

@Repository
public interface CategoriasDoProdRepository extends 
		JpaRepository<CategoriaDoProduto, Integer> {

	@Query(value = 
			"SELECT c "
			+ "FROM CategoriaDoProduto c "
			+ "ORDER BY c.id ")
	public List<CategoriaDoProduto> listarPor(Status status);
	
	@Query(value = 
			"SELECT c "
			+ "FROM CategoriaDoProduto c "
			+ "WHERE c.id = :id ")
	public CategoriaDoProduto buscarPor(Integer id);
	
}
